package com.example.ui.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.GoldPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    val transition = rememberInfiniteTransition(label = "SplashInfinite")
    val pulseScale by transition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    val haloRadius by transition.animateFloat(
        initialValue = 40f,
        targetValue = 110f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HaloRadius"
    )

    val haloAlpha by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HaloAlpha"
    )

    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "TextAlpha"
    )

    val textScale by animateFloatAsState(
        targetValue = if (startAnimation) 1.0f else 0.88f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "TextScale"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        // Stay on splash screen for 2.2 seconds then transition to main screen
        delay(2200)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { onSplashFinished() }
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        // Glowing animated aura in the center
        Box(
            modifier = Modifier
                .size((haloRadius * 2).dp)
                .scale(pulseScale)
                .alpha(haloAlpha)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.35f),
                            GoldPrimary.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Minimalist White Location Pin Icon
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .scale(textScale)
                    .alpha(textAlpha),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_launcher_asset),
                    contentDescription = "Urbe Logo Minimalista",
                    modifier = Modifier.size(76.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App Name: Solid Pure White on Pure Black background
            Text(
                text = "URBE",
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 8.sp,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .scale(textScale)
                    .alpha(textAlpha)
                    .testTag("splash_app_title")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Luxury Minimalist Subtitle
            Text(
                text = "MAPA COMERCIAL • RED VERIFICADA",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 3.sp,
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Subtle gold tracking accent bar
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(2.dp)
                    .alpha(textAlpha)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                GoldPrimary,
                                Color.White,
                                GoldPrimary,
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // Tap to skip hint at bottom
        Text(
            text = "Toca para entrar",
            color = Color.White.copy(alpha = 0.3f),
            fontSize = 11.sp,
            letterSpacing = 1.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 28.dp)
                .alpha(textAlpha)
        )
    }
}
