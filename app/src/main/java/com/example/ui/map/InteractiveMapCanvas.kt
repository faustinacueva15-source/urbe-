package com.example.ui.map

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.theme.*
import kotlin.math.sqrt

@Composable
fun InteractiveMapCanvas(
    businesses: List<Business>,
    poiGuides: List<MapPoiGuide>,
    userLocation: MapPoint,
    currentMapCenter: MapPoint,
    worldCities: List<WorldCity>,
    selectedCity: WorldCity,
    mapExploreMode: MapExploreMode,
    selectedBusiness: Business?,
    activeRouteBusiness: Business?,
    onSelectBusiness: (Business) -> Unit,
    onClearRoute: () -> Unit,
    onSelectWorldCity: (WorldCity) -> Unit,
    onRecenterUserLocation: () -> Unit,
    onOpenCreateBusinessAtLocation: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    // Zoom and pan offsets
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }
    var showGuides by remember { mutableStateOf(true) }
    var showReliefs by remember { mutableStateOf(true) }

    // Tap-to-create point marker state
    var tappedPointOnMap by remember { mutableStateOf<Offset?>(null) }
    var tappedPointCoords by remember { mutableStateOf<Pair<Double, Double>?>(null) }

    // Reset pan whenever map center changes (e.g. user selects a different city or recenters)
    LaunchedEffect(currentMapCenter) {
        panOffsetX = 0f
        panOffsetY = 0f
        tappedPointOnMap = null
        tappedPointCoords = null
    }

    // Pulsing radar animation for user location
    val infiniteTransition = rememberInfiniteTransition(label = "RadarTransition")
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 54f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarPulse"
    )
    val radarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarAlpha"
    )

    // Center base anchor coordinates
    val baseLat = currentMapCenter.lat
    val baseLng = currentMapCenter.lng

    val textMeasurer = rememberTextMeasurer()

    Box(modifier = modifier.fillMaxSize().background(MapLandColor)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        zoomScale = (zoomScale * zoom).coerceIn(0.28f, 4.0f)
                        panOffsetX += pan.x
                        panOffsetY += pan.y
                        tappedPointOnMap = null // Dismiss tapped pin when dragging
                    }
                }
                .pointerInput(businesses, worldCities, zoomScale, panOffsetX, panOffsetY, baseLat, baseLng) {
                    detectTapGestures(
                        onDoubleTap = {
                            zoomScale = (zoomScale * 1.4f).coerceAtMost(4.0f)
                        },
                        onLongPress = { tapOffset ->
                            val centerX = size.width / 2f + panOffsetX
                            val centerY = size.height / 2f + panOffsetY
                            val scale = 36000f * zoomScale
                            val clickedLng = baseLng + (tapOffset.x - centerX) / scale
                            val clickedLat = baseLat - (tapOffset.y - centerY) / scale
                            tappedPointOnMap = tapOffset
                            tappedPointCoords = Pair(clickedLat, clickedLng)
                        },
                        onTap = { tapOffset ->
                            val centerX = size.width / 2f + panOffsetX
                            val centerY = size.height / 2f + panOffsetY
                            val scale = 36000f * zoomScale

                            // 1. Check if a business pin was tapped
                            var foundBiz: Business? = null
                            for (biz in businesses) {
                                val px = centerX + ((biz.longitude - baseLng) * scale).toFloat()
                                val py = centerY - ((biz.latitude - baseLat) * scale).toFloat()
                                val dist = sqrt((tapOffset.x - px) * (tapOffset.x - px) + (tapOffset.y - py) * (tapOffset.y - py))
                                if (dist < 40f) {
                                    foundBiz = biz
                                    break
                                }
                            }

                            if (foundBiz != null) {
                                tappedPointOnMap = null
                                onSelectBusiness(foundBiz)
                                return@detectTapGestures
                            }

                            // 2. Check if a world city pin was tapped in global/zoomed out mode
                            if (zoomScale < 0.65f) {
                                for (city in worldCities) {
                                    val px = centerX + ((city.lng - baseLng) * scale).toFloat()
                                    val py = centerY - ((city.lat - baseLat) * scale).toFloat()
                                    val dist = sqrt((tapOffset.x - px) * (tapOffset.x - px) + (tapOffset.y - py) * (tapOffset.y - py))
                                    if (dist < 35f) {
                                        onSelectWorldCity(city)
                                        return@detectTapGestures
                                    }
                                }
                            }

                            // 3. Otherwise, set tap-to-create point
                            val clickedLng = baseLng + (tapOffset.x - centerX) / scale
                            val clickedLat = baseLat - (tapOffset.y - centerY) / scale
                            tappedPointOnMap = tapOffset
                            tappedPointCoords = Pair(clickedLat, clickedLng)
                        }
                    )
                }
                .testTag("interactive_map_canvas")
        ) {
            val canvasW = size.width
            val canvasH = size.height
            val centerX = canvasW / 2f + panOffsetX
            val centerY = canvasH / 2f + panOffsetY
            val scale = 36000f * zoomScale

            val isWorldView = zoomScale < 0.55f

            if (isWorldView) {
                // ==================== GLOBAL WORLD MAP VIEW ====================
                drawWorldView(centerX, centerY, scale, canvasW, canvasH, worldCities, textMeasurer)
            } else {
                // ==================== DETAILED LOCAL & REGIONAL MAP VIEW ====================

                // 1. Topographic Relieves & Elevation Contour Lines
                if (showReliefs) {
                    drawTopographicReliefs(centerX, centerY, scale, canvasW, canvasH)
                }

                // 2. Ocean / Coastal Water & Bluff Relinquishment
                drawCoastalReliefAndWater(centerX, centerY, scale, canvasW, canvasH, textMeasurer)

                // 3. City Blocks & 3D Extruded Buildings (Alivia la sensación de vacío)
                drawDetailedCityBlocksAndBuildings(centerX, centerY, scale, canvasW, canvasH)

                // 4. Parks, Green Walkways & Fountains
                drawParksAndRecreation(centerX, centerY, scale, textMeasurer)

                // 5. Complete Road Network with Street & Avenue Names
                drawCompleteRoadNetwork(centerX, centerY, scale, textMeasurer)

                // 6. Active Navigation Route Line
                if (activeRouteBusiness != null) {
                    drawActiveRoute(
                        centerX = centerX,
                        centerY = centerY,
                        scale = scale,
                        userLat = userLocation.lat,
                        userLng = userLocation.lng,
                        targetLat = activeRouteBusiness.latitude,
                        targetLng = activeRouteBusiness.longitude
                    )
                }

                // 7. POI Landmark Guides
                if (showGuides) {
                    for (poi in poiGuides) {
                        val px = centerX + ((poi.lng - baseLng) * scale).toFloat()
                        val py = centerY - ((poi.lat - baseLat) * scale).toFloat()
                        drawPoiGuide(px, py, poi.title, poi.typeName, textMeasurer)
                    }
                }

                // 8. User GPS Location Beacon (Only if user location is within visible radius)
                val userDistFromCenter = sqrt(
                    (userLocation.lat - baseLat) * (userLocation.lat - baseLat) +
                    (userLocation.lng - baseLng) * (userLocation.lng - baseLng)
                )
                if (userDistFromCenter < 0.08) {
                    val userPx = centerX + ((userLocation.lng - baseLng) * scale).toFloat()
                    val userPy = centerY - ((userLocation.lat - baseLat) * scale).toFloat()

                    drawCircle(
                        color = CobaltBlue.copy(alpha = radarAlpha),
                        radius = radarPulse * zoomScale.coerceIn(0.8f, 1.8f),
                        center = Offset(userPx, userPy)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 9f,
                        center = Offset(userPx, userPy)
                    )
                    drawCircle(
                        color = CobaltBlue,
                        radius = 7f,
                        center = Offset(userPx, userPy)
                    )
                }

                // 9. Business Markers
                for (biz in businesses) {
                    val px = centerX + ((biz.longitude - baseLng) * scale).toFloat()
                    val py = centerY - ((biz.latitude - baseLat) * scale).toFloat()
                    val isSelected = selectedBusiness?.id == biz.id

                    drawBusinessMarker(
                        px = px,
                        py = py,
                        business = biz,
                        isSelected = isSelected,
                        textMeasurer = textMeasurer
                    )
                }

                // 10. Tapped Point Pin (Tap-to-create affordance)
                tappedPointOnMap?.let { tapPos ->
                    drawCircle(
                        color = GoldPrimary.copy(alpha = 0.4f),
                        radius = 16f,
                        center = tapPos
                    )
                    drawCircle(
                        color = GoldPrimary,
                        radius = 8f,
                        center = tapPos
                    )
                    drawCircle(
                        color = ObsidianDark,
                        radius = 4f,
                        center = tapPos
                    )
                }
            }
        }

        // Floating Tap-to-Create Callout Pill
        if (tappedPointOnMap != null && tappedPointCoords != null) {
            val coords = tappedPointCoords!!
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SlateNavyDark,
                tonalElevation = 10.dp,
                shadowElevation = 12.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldPrimary),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 90.dp)
                    .testTag("tap_to_create_callout")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AddLocationAlt, null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Punto seleccionado en el mapa",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimaryDark)
                        )
                        Text(
                            text = "Lat: ${String.format("%.4f", coords.first)}, Lng: ${String.format("%.4f", coords.second)}",
                            style = MaterialTheme.typography.labelSmall.copy(color = GoldLight, fontSize = 10.sp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            onOpenCreateBusinessAtLocation(coords.first, coords.second)
                            tappedPointOnMap = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = ObsidianDark),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("+ Crear Negocio", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = { tappedPointOnMap = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, null, tint = TextSecondaryDark, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Active Navigation Floating Header Banner
        if (activeRouteBusiness != null) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = SlateNavyDark,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 110.dp)
                    .testTag("active_route_banner")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Ruta hacia ${activeRouteBusiness.name}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryDark
                            )
                        )
                        Text(
                            text = "450 m • 6 min a pie • Por Av. José Larco",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = GoldLight
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    IconButton(
                        onClick = onClearRoute,
                        modifier = Modifier.size(32.dp).testTag("clear_route_button")
                    ) {
                        Icon(Icons.Default.Close, null, tint = TextSecondaryDark, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }

        // World Metropolises Horizontal Quick Bar
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = SlateNavyDark.copy(alpha = 0.95f),
            tonalElevation = 6.dp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 62.dp, start = 16.dp, end = 16.dp)
                .testTag("world_cities_bar")
        ) {
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explorar:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                worldCities.forEach { city ->
                    val isCurrent = selectedCity.id == city.id
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isCurrent) GoldPrimary else SurfaceCardDark,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectWorldCity(city) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(city.flagEmoji, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (city.isUserLocation) "Mi Zona (${city.name})" else city.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isCurrent) ObsidianDark else TextPrimaryDark,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Floating Map Controls (Right Side)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Re-center on User Location
            FloatingActionButton(
                onClick = onRecenterUserLocation,
                containerColor = SlateNavyDark,
                contentColor = GoldPrimary,
                shape = CircleShape,
                modifier = Modifier
                    .size(46.dp)
                    .shadow(6.dp, CircleShape)
                    .testTag("recenter_location_button")
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Mi Ubicación",
                    modifier = Modifier.size(22.dp)
                )
            }

            // Zoom In (+)
            SmallFloatingActionButton(
                onClick = { zoomScale = (zoomScale * 1.3f).coerceAtMost(4.0f) },
                containerColor = SlateNavyDark,
                contentColor = TextPrimaryDark,
                shape = CircleShape,
                modifier = Modifier.size(40.dp).shadow(4.dp, CircleShape).testTag("zoom_in_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Acercar")
            }

            // Zoom Out (-)
            SmallFloatingActionButton(
                onClick = { zoomScale = (zoomScale / 1.3f).coerceAtLeast(0.28f) },
                containerColor = SlateNavyDark,
                contentColor = TextPrimaryDark,
                shape = CircleShape,
                modifier = Modifier.size(40.dp).shadow(4.dp, CircleShape).testTag("zoom_out_button")
            ) {
                Icon(imageVector = Icons.Default.Remove, contentDescription = "Alejar")
            }

            // Toggle Topographic Relinquishments
            SmallFloatingActionButton(
                onClick = { showReliefs = !showReliefs },
                containerColor = if (showReliefs) GoldContainer else SlateNavyDark,
                contentColor = if (showReliefs) GoldPrimary else TextSecondaryDark,
                shape = CircleShape,
                modifier = Modifier.size(40.dp).shadow(4.dp, CircleShape).testTag("toggle_reliefs_button")
            ) {
                Icon(Icons.Default.Terrain, contentDescription = "Relieves")
            }

            // Toggle Guides & Vías
            SmallFloatingActionButton(
                onClick = { showGuides = !showGuides },
                containerColor = if (showGuides) GoldContainer else SlateNavyDark,
                contentColor = if (showGuides) GoldPrimary else TextSecondaryDark,
                shape = CircleShape,
                modifier = Modifier.size(40.dp).shadow(4.dp, CircleShape).testTag("toggle_guides_button")
            ) {
                Icon(
                    imageVector = if (showGuides) Icons.Default.AltRoute else Icons.Default.LayersClear,
                    contentDescription = "Guías y Vías"
                )
            }
        }

        // Luxury Map Status Pill (Bottom Left)
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = SlateNavyDark.copy(alpha = 0.92f),
            border = androidx.compose.foundation.BorderStroke(1.dp, GoldContainer),
            tonalElevation = 6.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = if (selectedBusiness != null) 250.dp else 70.dp)
                .clip(RoundedCornerShape(14.dp))
                .clickable {
                    zoomScale = 1.0f
                    panOffsetX = 0f
                    panOffsetY = 0f
                }
                .testTag("map_status_indicator_pill")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Default.Explore, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(14.dp))
                Text(
                    text = "${selectedCity.name} • ${String.format("%.1f", zoomScale)}x",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = TextPrimaryDark, fontSize = 10.5.sp)
                )
                if (showReliefs) {
                    Surface(shape = RoundedCornerShape(4.dp), color = GoldContainer) {
                        Text(
                            text = "3D",
                            style = MaterialTheme.typography.labelSmall.copy(color = GoldLight, fontSize = 9.sp, fontWeight = FontWeight.Black),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==================== DRAWING ENHANCEMENTS: RELIEFS & URBAN DETAILS ====================

private fun DrawScope.drawTopographicReliefs(
    centerX: Float,
    centerY: Float,
    scale: Float,
    canvasW: Float,
    canvasH: Float
) {
    // 1. Shaded Highland elevation zones (Colinas y mesetas urbanas)
    val hill1Path = Path().apply {
        val hX = centerX + 0.0020f * scale
        val hY = centerY - 0.0025f * scale
        addOval(androidx.compose.ui.geometry.Rect(hX - 180f, hY - 120f, hX + 220f, hY + 140f))
    }
    drawPath(hill1Path, color = MapReliefHighlandColor)

    // 2. Contour lines (Curvas de nivel topográficas para dar textura de relieve al terreno)
    val contourColor = MapReliefContourColor
    val contourStroke = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f))

    for (i in 1..4) {
        val radiusX = (120f + i * 45f) * (scale / 36000f)
        val radiusY = (80f + i * 30f) * (scale / 36000f)
        drawOval(
            color = contourColor,
            topLeft = Offset(centerX + 0.0020f * scale - radiusX, centerY - 0.0025f * scale - radiusY),
            size = Size(radiusX * 2, radiusY * 2),
            style = contourStroke
        )
    }

    // Coastal Bluff / Acantilado Contours (Relieve de acantilado hacia el mar)
    val coastX = centerX - 0.0055f * scale
    val cliffPath = Path().apply {
        moveTo(coastX, -100f)
        cubicTo(
            coastX - 35f, canvasH * 0.35f,
            coastX + 55f, canvasH * 0.65f,
            coastX - 15f, canvasH + 100f
        )
        lineTo(coastX + 45f, canvasH + 100f)
        cubicTo(
            coastX + 115f, canvasH * 0.65f,
            coastX + 25f, canvasH * 0.35f,
            coastX + 60f, -100f
        )
        close()
    }
    drawPath(cliffPath, color = MapReliefHighlandColor.copy(alpha = 0.85f))
}

private fun DrawScope.drawCoastalReliefAndWater(
    centerX: Float,
    centerY: Float,
    scale: Float,
    canvasW: Float,
    canvasH: Float,
    textMeasurer: TextMeasurer
) {
    val coastX = centerX - 0.0055f * scale

    // Shallow Coastal Water Strip
    val shallowWaterPath = Path().apply {
        moveTo(-100f, -100f)
        lineTo(coastX + 20f, -100f)
        cubicTo(
            coastX - 15f, canvasH * 0.3f,
            coastX + 85f, canvasH * 0.7f,
            coastX + 5f, canvasH + 100f
        )
        lineTo(-100f, canvasH + 100f)
        close()
    }
    drawPath(shallowWaterPath, color = MapWaterShallowColor)

    // Deep Ocean Water
    val deepWaterPath = Path().apply {
        moveTo(-100f, -100f)
        lineTo(coastX - 25f, -100f)
        cubicTo(
            coastX - 60f, canvasH * 0.3f,
            coastX + 40f, canvasH * 0.7f,
            coastX - 40f, canvasH + 100f
        )
        lineTo(-100f, canvasH + 100f)
        close()
    }
    drawPath(deepWaterPath, color = MapWaterColor)

    // Ocean Waves and Labels
    val textStyle = TextStyle(
        color = CobaltBlue.copy(alpha = 0.5f),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 2.sp
    )
    drawText(
        textMeasurer = textMeasurer,
        text = "OCÉANO PACÍFICO",
        topLeft = Offset(coastX - 160f, canvasH * 0.45f),
        style = textStyle
    )
}

private fun DrawScope.drawDetailedCityBlocksAndBuildings(
    centerX: Float,
    centerY: Float,
    scale: Float,
    canvasW: Float,
    canvasH: Float
) {
    val blockStep = 80f * (scale / 36000f)
    val startX = (centerX % blockStep) - blockStep
    val startY = (centerY % blockStep) - blockStep

    var x = startX
    while (x < canvasW + blockStep) {
        var y = startY
        while (y < canvasH + blockStep) {
            // Block ground lot
            val lotW = blockStep - 16f
            val lotH = blockStep - 16f
            drawRoundRect(
                color = MapBlockColor,
                topLeft = Offset(x + 8f, y + 8f),
                size = Size(lotW, lotH),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
            )

            // Extruded 3D Building Rooftops inside each block to add depth and life
            if (scale > 24000f) {
                // Building Facade Shadow
                drawRoundRect(
                    color = MapBuildingFacadeColor,
                    topLeft = Offset(x + 14f, y + 16f),
                    size = Size(lotW - 12f, lotH - 12f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )
                // Building Top Face
                drawRoundRect(
                    color = MapBuildingTopColor,
                    topLeft = Offset(x + 14f, y + 12f),
                    size = Size(lotW - 12f, lotH - 14f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                )
            }

            y += blockStep
        }
        x += blockStep
    }
}

private fun DrawScope.drawParksAndRecreation(
    centerX: Float,
    centerY: Float,
    scale: Float,
    textMeasurer: TextMeasurer
) {
    // 1. Central Metropolitan Park (Parque Central de Miraflores / Kennedy)
    val park1X = centerX - 0.0008f * scale
    val park1Y = centerY - 0.0012f * scale
    val park1W = 150f * (scale / 36000f)
    val park1H = 110f * (scale / 36000f)

    drawRoundRect(
        color = MapParkColor,
        topLeft = Offset(park1X, park1Y),
        size = Size(park1W, park1H),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14f, 14f)
    )
    drawRoundRect(
        color = MapParkAccentColor,
        topLeft = Offset(park1X + 10f, park1Y + 10f),
        size = Size(park1W - 20f, park1H - 20f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
    )
    // Central Fountain in park
    drawCircle(
        color = CobaltBlue,
        radius = 8f,
        center = Offset(park1X + park1W / 2f, park1Y + park1H / 2f)
    )

    // 2. Coastal Salazar Park & Malecón
    val park2X = centerX - 0.0045f * scale
    val park2Y = centerY + 0.0018f * scale
    val park2W = 170f * (scale / 36000f)
    val park2H = 85f * (scale / 36000f)

    drawRoundRect(
        color = MapParkColor,
        topLeft = Offset(park2X, park2Y),
        size = Size(park2W, park2H),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f)
    )

    // Park labels
    val parkStyle = TextStyle(
        color = EmeraldTrust,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold
    )
    drawText(
        textMeasurer = textMeasurer,
        text = "🌿 Parque Central Kennedy",
        topLeft = Offset(park1X + 16f, park1Y + 16f),
        style = parkStyle
    )
    drawText(
        textMeasurer = textMeasurer,
        text = "🌳 Parque del Amor & Malecón",
        topLeft = Offset(park2X + 16f, park2Y + 14f),
        style = parkStyle
    )
}

private fun DrawScope.drawCompleteRoadNetwork(
    centerX: Float,
    centerY: Float,
    scale: Float,
    textMeasurer: TextMeasurer
) {
    val roadStep = 80f * (scale / 36000f)

    // ================= MAIN AVENUES =================

    // 1. Av. Del Parque (Main Horizontal Avenue)
    val avParqueY = centerY
    drawLine(
        color = MapHighwayColor,
        start = Offset(-200f, avParqueY),
        end = Offset(size.width + 200f, avParqueY),
        strokeWidth = 18f * (scale / 36000f)
    )
    drawLine(
        color = MapRoadColor,
        start = Offset(-200f, avParqueY),
        end = Offset(size.width + 200f, avParqueY),
        strokeWidth = 14f * (scale / 36000f)
    )

    // 2. Paseo de la República / Vía Expresa (Main Vertical Expressway)
    val hwyX = centerX + 0.0032f * scale
    drawLine(
        color = MapHighwayColor,
        start = Offset(hwyX, -200f),
        end = Offset(hwyX, size.height + 200f),
        strokeWidth = 24f * (scale / 36000f)
    )
    drawLine(
        color = MapRoadColor,
        start = Offset(hwyX, -200f),
        end = Offset(hwyX, size.height + 200f),
        strokeWidth = 18f * (scale / 36000f)
    )
    // Expressway dashed median lane
    drawLine(
        color = AmberPending,
        start = Offset(hwyX, -200f),
        end = Offset(hwyX, size.height + 200f),
        strokeWidth = 2f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f), 0f)
    )

    // 3. Av. José Larco (Diagonal Main Avenue)
    val larcoStart = Offset(centerX - 0.0035f * scale, centerY + 0.0035f * scale)
    val larcoEnd = Offset(centerX + 0.0025f * scale, centerY - 0.0025f * scale)
    drawLine(
        color = MapHighwayColor,
        start = larcoStart,
        end = larcoEnd,
        strokeWidth = 16f * (scale / 36000f)
    )
    drawLine(
        color = MapRoadColor,
        start = larcoStart,
        end = larcoEnd,
        strokeWidth = 12f * (scale / 36000f)
    )

    // 4. Malecón de la Reserva (Coastal scenic avenue along the bluffs)
    val maleconCoastX = centerX - 0.0050f * scale
    val maleconPath = Path().apply {
        moveTo(maleconCoastX, -100f)
        cubicTo(
            maleconCoastX - 35f, size.height * 0.35f,
            maleconCoastX + 55f, size.height * 0.65f,
            maleconCoastX - 15f, size.height + 100f
        )
    }
    drawPath(
        path = maleconPath,
        color = MapHighwayColor,
        style = Stroke(width = 14f * (scale / 36000f), cap = StrokeCap.Round)
    )
    drawPath(
        path = maleconPath,
        color = MapRoadColor,
        style = Stroke(width = 10f * (scale / 36000f), cap = StrokeCap.Round)
    )

    // 5. Av. Santa Cruz (Northern boulevard)
    val santaCruzY = centerY - 0.0028f * scale
    drawLine(
        color = MapHighwayColor,
        start = Offset(-200f, santaCruzY),
        end = Offset(size.width + 200f, santaCruzY),
        strokeWidth = 14f * (scale / 36000f)
    )
    drawLine(
        color = MapRoadColor,
        start = Offset(-200f, santaCruzY),
        end = Offset(size.width + 200f, santaCruzY),
        strokeWidth = 10f * (scale / 36000f)
    )

    // Secondary Street Grid
    var x = (centerX % roadStep) - roadStep
    while (x < size.width + roadStep) {
        drawLine(
            color = MapRoadColor.copy(alpha = 0.7f),
            start = Offset(x, -50f),
            end = Offset(x, size.height + 50f),
            strokeWidth = 6f
        )
        x += roadStep
    }

    var y = (centerY % roadStep) - roadStep
    while (y < size.height + roadStep) {
        drawLine(
            color = MapRoadColor.copy(alpha = 0.7f),
            start = Offset(-50f, y),
            end = Offset(size.width + 50f, y),
            strokeWidth = 6f
        )
        y += roadStep
    }

    // ================= ROTULACIÓN DE CALLES Y AVENIDAS =================
    val mainAveStyle = TextStyle(
        color = TextPrimaryDark,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp
    )
    val streetStyle = TextStyle(
        color = TextSecondaryDark,
        fontSize = 8.5.sp,
        fontWeight = FontWeight.Medium
    )

    // 1. Av. Del Parque
    drawStreetNameLabel(
        textMeasurer = textMeasurer,
        name = "Av. Del Parque",
        pos = Offset(centerX - 120f, avParqueY - 18f),
        style = mainAveStyle
    )

    // 2. Vía Expresa Paseo de la República
    drawStreetNameLabel(
        textMeasurer = textMeasurer,
        name = "Vía Expresa Central",
        pos = Offset(hwyX + 14f, centerY + 60f),
        style = mainAveStyle
    )

    // 3. Av. José Larco
    drawStreetNameLabel(
        textMeasurer = textMeasurer,
        name = "Av. José Larco",
        pos = Offset(centerX - 60f, centerY + 80f),
        style = mainAveStyle
    )

    // 4. Malecón de la Reserva
    drawStreetNameLabel(
        textMeasurer = textMeasurer,
        name = "Malecón de la Reserva",
        pos = Offset(maleconCoastX + 12f, size.height * 0.35f),
        style = mainAveStyle
    )

    // 5. Av. Santa Cruz
    drawStreetNameLabel(
        textMeasurer = textMeasurer,
        name = "Av. Santa Cruz",
        pos = Offset(centerX - 100f, santaCruzY - 16f),
        style = mainAveStyle
    )

    // Secondary Street Labels
    drawStreetNameLabel(textMeasurer, "Calle Schell", Offset(centerX + 60f, avParqueY + 55f), streetStyle)
    drawStreetNameLabel(textMeasurer, "Calle San Martín", Offset(centerX - 140f, avParqueY + 65f), streetStyle)
    drawStreetNameLabel(textMeasurer, "Calle Bellavista", Offset(centerX + 80f, avParqueY - 65f), streetStyle)
    drawStreetNameLabel(textMeasurer, "Calle Los Sauces", Offset(centerX - 160f, avParqueY - 70f), streetStyle)
    drawStreetNameLabel(textMeasurer, "Av. Angamos Oeste", Offset(centerX - 150f, centerY - 140f), streetStyle)
    drawStreetNameLabel(textMeasurer, "Av. Mariscal Benavides", Offset(centerX - 80f, avParqueY + 130f), mainAveStyle)
    drawStreetNameLabel(textMeasurer, "Pasaje San Ramón (Pizzas)", Offset(centerX + 35f, centerY - 15f), streetStyle)
    drawStreetNameLabel(textMeasurer, "Calle Esperanza", Offset(centerX - 90f, centerY + 25f), streetStyle)
    drawStreetNameLabel(textMeasurer, "Calle Cantuarias", Offset(centerX + 90f, centerY - 32f), streetStyle)
    drawStreetNameLabel(textMeasurer, "Calle Mártir Olaya", Offset(centerX + 20f, centerY + 95f), streetStyle)
}

private fun DrawScope.drawStreetNameLabel(
    textMeasurer: TextMeasurer,
    name: String,
    pos: Offset,
    style: TextStyle
) {
    val layout = textMeasurer.measure(text = name, style = style)
    drawRoundRect(
        color = SlateNavyDark.copy(alpha = 0.85f),
        topLeft = Offset(pos.x - 4f, pos.y - 2f),
        size = Size(layout.size.width + 8f, layout.size.height + 4f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
    )
    drawText(
        textLayoutResult = layout,
        topLeft = pos
    )
}

// ================= GLOBAL WORLD VIEW =================
private fun DrawScope.drawWorldView(
    centerX: Float,
    centerY: Float,
    scale: Float,
    canvasW: Float,
    canvasH: Float,
    worldCities: List<WorldCity>,
    textMeasurer: TextMeasurer
) {
    // Deep world ocean background
    drawRect(color = MapWaterColor)

    // Global Latitude & Longitude Coordinate Grid (Meridianos y paralelos)
    val gridColor = MapHighwayColor.copy(alpha = 0.25f)
    for (i in 0..8) {
        val y = canvasH * (i / 8f)
        drawLine(color = gridColor, start = Offset(0f, y), end = Offset(canvasW, y), strokeWidth = 1f)
    }
    for (i in 0..12) {
        val x = canvasW * (i / 12f)
        drawLine(color = gridColor, start = Offset(x, 0f), end = Offset(x, canvasH), strokeWidth = 1f)
    }

    // World Header Badge
    val headerLayout = textMeasurer.measure(
        text = "VISTA CARTOGRÁFICA MUNDIAL • SELECCIONA UNA METRÓPOLIS",
        style = TextStyle(color = GoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
    )
    drawRoundRect(
        color = SlateNavyDark.copy(alpha = 0.9f),
        topLeft = Offset(canvasW / 2f - headerLayout.size.width / 2f - 12f, 140f),
        size = Size(headerLayout.size.width + 24f, headerLayout.size.height + 10f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
    )
    drawText(
        textLayoutResult = headerLayout,
        topLeft = Offset(canvasW / 2f - headerLayout.size.width / 2f, 145f)
    )

    // World Cities Pins with Flags
    for (city in worldCities) {
        // Project world lat/lng onto canvas center
        val px = centerX + (city.lng * (scale / 400f)).toFloat()
        val py = centerY - (city.lat * (scale / 400f)).toFloat()

        // Pin Glow
        drawCircle(
            color = if (city.isUserLocation) EmeraldTrust.copy(alpha = 0.4f) else GoldPrimary.copy(alpha = 0.3f),
            radius = 20f,
            center = Offset(px, py)
        )
        drawCircle(
            color = if (city.isUserLocation) EmeraldTrust else GoldPrimary,
            radius = 10f,
            center = Offset(px, py)
        )
        drawCircle(
            color = ObsidianDark,
            radius = 5f,
            center = Offset(px, py)
        )

        // City Label with Flag
        val cityText = "${city.flagEmoji} ${city.name}"
        val cityLayout = textMeasurer.measure(
            text = cityText,
            style = TextStyle(color = TextPrimaryDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        )
        drawRoundRect(
            color = SlateNavyDark,
            topLeft = Offset(px - cityLayout.size.width / 2f - 6f, py + 14f),
            size = Size(cityLayout.size.width + 12f, cityLayout.size.height + 6f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f)
        )
        drawText(
            textLayoutResult = cityLayout,
            topLeft = Offset(px - cityLayout.size.width / 2f, py + 16f)
        )
    }
}

private fun DrawScope.drawActiveRoute(
    centerX: Float,
    centerY: Float,
    scale: Float,
    userLat: Double,
    userLng: Double,
    targetLat: Double,
    targetLng: Double
) {
    val startX = centerX
    val startY = centerY
    val endX = centerX + ((targetLng - userLng) * scale).toFloat()
    val endY = centerY - ((targetLat - userLat) * scale).toFloat()

    val midX = endX
    val midY = startY

    val routePath = Path().apply {
        moveTo(startX, startY)
        lineTo(midX, midY)
        lineTo(endX, endY)
    }

    drawPath(
        path = routePath,
        color = GoldPrimary.copy(alpha = 0.35f),
        style = Stroke(width = 10f, cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
    drawPath(
        path = routePath,
        color = GoldPrimary,
        style = Stroke(
            width = 5f,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 12f), 0f)
        )
    )
    drawCircle(
        color = GoldPrimary,
        radius = 6f,
        center = Offset(midX, midY)
    )
}

private fun DrawScope.drawPoiGuide(
    px: Float,
    py: Float,
    title: String,
    typeName: String,
    textMeasurer: TextMeasurer
) {
    drawCircle(color = AmberPending, radius = 5f, center = Offset(px, py))
    drawCircle(color = SlateNavyDark, radius = 2.5f, center = Offset(px, py))

    val labelText = "$title • $typeName"
    val layout = textMeasurer.measure(
        text = labelText,
        style = TextStyle(color = TextSecondaryDark, fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
    )
    drawRoundRect(
        color = SlateNavyDark.copy(alpha = 0.85f),
        topLeft = Offset(px - layout.size.width / 2f - 4f, py - 20f),
        size = Size(layout.size.width + 8f, layout.size.height + 4f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
    )
    drawText(
        textLayoutResult = layout,
        topLeft = Offset(px - layout.size.width / 2f, py - 18f)
    )
}

private fun DrawScope.drawBusinessMarker(
    px: Float,
    py: Float,
    business: Business,
    isSelected: Boolean,
    textMeasurer: TextMeasurer
) {
    val markerRadius = if (isSelected) 18f else 14f

    if (isSelected) {
        drawCircle(
            color = GoldPrimary.copy(alpha = 0.35f),
            radius = markerRadius + 10f,
            center = Offset(px, py)
        )
    }

    val ringColor = if (business.reputationScore >= 95) GoldPrimary else EmeraldTrust
    drawCircle(color = ringColor, radius = markerRadius + 2.5f, center = Offset(px, py))
    drawCircle(color = SlateNavyDark, radius = markerRadius, center = Offset(px, py))

    val categorySymbol = when (business.category) {
        BusinessCategory.BARBERIA -> "💈"
        BusinessCategory.REPOSTERIA -> "🍰"
        BusinessCategory.BODEGA -> "🏪"
        BusinessCategory.CAFETERIA -> "☕"
        BusinessCategory.RESTAURANTE -> "🍽️"
        BusinessCategory.FARMACIA -> "💊"
        BusinessCategory.GOURMET -> "🍷"
    }

    val symbolLayout = textMeasurer.measure(
        text = categorySymbol,
        style = TextStyle(fontSize = if (isSelected) 12.sp else 10.sp)
    )
    drawText(
        textLayoutResult = symbolLayout,
        topLeft = Offset(px - symbolLayout.size.width / 2f, py - symbolLayout.size.height / 2f)
    )

    // Open/Closed indicator dot on top-right
    val statusColor = if (business.isOpen) EmeraldTrust else RubyAlert
    drawCircle(
        color = statusColor,
        radius = 4f,
        center = Offset(px + markerRadius * 0.7f, py - markerRadius * 0.7f)
    )

    // Name label if selected
    if (isSelected) {
        val nameText = "${business.name} (★ ${business.rating})"
        val nameLayout = textMeasurer.measure(
            text = nameText,
            style = TextStyle(color = TextPrimaryDark, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        )
        val pillW = nameLayout.size.width + 16f
        val pillH = nameLayout.size.height + 8f

        drawRoundRect(
            color = ObsidianDark.copy(alpha = 0.95f),
            topLeft = Offset(px - pillW / 2f, py + markerRadius + 6f),
            size = Size(pillW, pillH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
        )
        drawRoundRect(
            color = GoldPrimary,
            topLeft = Offset(px - pillW / 2f, py + markerRadius + 6f),
            size = Size(pillW, pillH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f),
            style = Stroke(width = 1.5f)
        )
        drawText(
            textLayoutResult = nameLayout,
            topLeft = Offset(px - nameLayout.size.width / 2f, py + markerRadius + 10f)
        )
    }
}
