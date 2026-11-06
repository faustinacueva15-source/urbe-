package com.example.data

import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class UrbeRepository {

    // Initial verified seed businesses
    private val initialBusinesses = listOf(
        Business(
            id = "biz_barber_1",
            name = "Imperio Real Barber Studio",
            category = BusinessCategory.BARBERIA,
            description = "Barbería ejecutiva de alta gama especializada en cortes de precisión, afeitado tradicional a navaja caliente y tratamientos capilares premium.",
            rating = 4.9,
            reviewCount = 184,
            address = "Av. Del Parque 450, Miraflores",
            latitude = -12.1215,
            longitude = -77.0298,
            phone = "+51 987 654 321",
            openingHours = "Lun - Sáb: 09:00 - 20:30",
            isOpen = true,
            isVisibleInApp = true,
            verificationStatus = VerificationStatus.VERIFICADO_PUBLICADO,
            reputationScore = 99,
            reputationVerdict = "Reputación Comercial Sobresaliente",
            verifiedByAdminDate = "15 Oct 2026",
            ownerName = "Carlos Mendoza Silva",
            ownerEmail = "carlos@imperioreal.pe",
            documents = listOf(
                VerificationDocument("doc_1", DocumentType.RUC_TRIBUTARIO, "20608933211", "Certificado_RUC_Activo.pdf", "10 Oct 2026", true, "RUC activo, sin observaciones tributarias"),
                VerificationDocument("doc_2", DocumentType.LICENCIA_MUNICIPAL, "LIC-4401-2024", "Licencia_Metropolitana_Vigente.pdf", "11 Oct 2026", true, "Aforo 20 personas, giro comercial válido"),
                VerificationDocument("doc_3", DocumentType.CERTIFICADO_SANITARIO, "CS-991-DIRIS", "Inspeccion_Higiene_Aprobada.pdf", "12 Oct 2026", true, "Esterilización de navajas certificada"),
                VerificationDocument("doc_4", DocumentType.DNI_REPRESENTANTE, "71223940", "DNI_Titular_Validado.pdf", "10 Oct 2026", true, "Biometría y firma conforme")
            ),
            promotions = listOf(
                Promotion("p_1", "biz_barber_1", "Ritual Barba & Corte Ejecutivo 20% OFF", "Descuento en tu visita agendada desde la app", 20, "URBEBARBER", "Válido hoy", true)
            ),
            menuItems = listOf(
                MenuItem("m_1", "biz_barber_1", "Corte Ejecutivo & Texturizado", "Lavado con exfoliación, asesoría de imagen y estilizado con cera mate", 45.0, "Cortes", true, badge = "Top Elegido"),
                MenuItem("m_2", "biz_barber_1", "Ritual Barba Spa & Toalla Caliente", "Aceites esenciales, perfilado milimétrico y bálsamo hidratante", 35.0, "Barba", true, 42.0),
                MenuItem("m_3", "biz_barber_1", "Combo Full Grooming VIP", "Corte + Barba + Mascarilla Black Detox + Bebida de cortesía", 70.0, "Combos", true, 85.0, badge = "20% OFF"),
                MenuItem("m_4", "biz_barber_1", "Tratamiento Anticaída & Masaje Craneal", "Tonificación del cuero cabelludo con ozonoterapia", 50.0, "Tratamientos", true)
            )
        ),
        Business(
            id = "biz_reposteria_1",
            name = "Dulce Atelier Repostería Fina",
            category = BusinessCategory.REPOSTERIA,
            description = "Pastelería francesa y postres artesanales horneados a diario. Utilizamos cacao al 70%, frutas de estación e insumos 100% naturales.",
            rating = 4.8,
            reviewCount = 246,
            address = "Calle Los Sauces 182, San Isidro",
            latitude = -12.1190,
            longitude = -77.0325,
            phone = "+51 912 345 678",
            openingHours = "Mar - Dom: 08:30 - 21:00",
            isOpen = true,
            isVisibleInApp = true,
            verificationStatus = VerificationStatus.VERIFICADO_PUBLICADO,
            reputationScore = 98,
            reputationVerdict = "Calidad Sanitaria y Comercial Certificada",
            verifiedByAdminDate = "20 Oct 2026",
            ownerName = "Valeria Arismendi",
            ownerEmail = "contacto@dulceatelier.pe",
            documents = listOf(
                VerificationDocument("doc_11", DocumentType.RUC_TRIBUTARIO, "20551984210", "Ficha_RUC_Atelier.pdf", "18 Oct 2026", true, "Régimen especial con libros electrónicos"),
                VerificationDocument("doc_12", DocumentType.LICENCIA_MUNICIPAL, "LIC-3218-2023", "Licencia_Comercial_Alimentos.pdf", "19 Oct 2026", true, "Cumple zonificación y ductos de extracción"),
                VerificationDocument("doc_13", DocumentType.CERTIFICADO_SANITARIO, "DIGESA-2026-F09", "Certificado_DIGESA_Inocuidad.pdf", "19 Oct 2026", true, "Buenas Prácticas de Manufactura BPM"),
                VerificationDocument("doc_14", DocumentType.DNI_REPRESENTANTE, "45892110", "DNI_Valeria_Arismendi.pdf", "18 Oct 2026", true, "Titular verificado")
            ),
            promotions = listOf(
                Promotion("p_2", "biz_reposteria_1", "2x1 en Porciones de Cheesecake", "Válido en Cheesecake Vasco y Maracuyá", 50, "DULCE2X1", "Solo por hoy", true)
            ),
            menuItems = listOf(
                MenuItem("m_11", "biz_reposteria_1", "Cheesecake Vasco Cremoso Caramelizado", "Porción generosa de queso horneado al estilo Donostia con coulis de frutos rojos", 22.0, "Tartas & Pasteles", true, badge = "Especialidad"),
                MenuItem("m_12", "biz_reposteria_1", "Tarta de Pistacho de Bronte & Frambuesas", "Masa sableé crujiente, ganache montada de pistacho y frambuesas frescas", 26.0, "Tartas & Pasteles", true),
                MenuItem("m_13", "biz_reposteria_1", "Caja Degustación 6 Macarons Parisinos", "Sabores: Vainilla Bourbon, Pistacho, Chocolate Belga, Frambuesa, Caramelo Salé", 32.0, "Petits Fours", true, 38.0, badge = "Regalo Ideal"),
                MenuItem("m_14", "biz_reposteria_1", "Croissant Crujiente de Almendras", "Doble horneado relleno con crema frangipane y almendras tostadas", 14.0, "Bollería Fina", true)
            )
        ),
        Business(
            id = "biz_bodega_1",
            name = "Bodega & Minimarket Don Santiago",
            category = BusinessCategory.BODEGA,
            description = "Tu bodega de barrio de confianza con surtido selecto de abarrotes de calidad, bebidas frías, vinos selectos, quesos artesanales y productos frescos.",
            rating = 4.7,
            reviewCount = 135,
            address = "Paseo de la República 1102",
            latitude = -12.1240,
            longitude = -77.0270,
            phone = "+51 998 877 665",
            openingHours = "Lun - Dom: 07:00 - 23:00",
            isOpen = true,
            isVisibleInApp = true,
            verificationStatus = VerificationStatus.VERIFICADO_PUBLICADO,
            reputationScore = 96,
            reputationVerdict = "Comercio de Proximidad Verificado",
            verifiedByAdminDate = "22 Oct 2026",
            ownerName = "Santiago Huamán Prado",
            ownerEmail = "bodega.donsantiago@gmail.com",
            documents = listOf(
                VerificationDocument("doc_21", DocumentType.RUC_TRIBUTARIO, "10448923019", "Ficha_RUC_Santiago.pdf", "21 Oct 2026", true, "RUC personal con negocio"),
                VerificationDocument("doc_22", DocumentType.LICENCIA_MUNICIPAL, "LIC-1022-2021", "Licencia_Bodega_Abarrotes.pdf", "21 Oct 2026", true, "Licencia definitiva al día"),
                VerificationDocument("doc_23", DocumentType.DNI_REPRESENTANTE, "09843211", "DNI_Santiago_Huaman.pdf", "21 Oct 2026", true, "Identidad validada")
            ),
            promotions = listOf(
                Promotion("p_3", "biz_bodega_1", "Delivery Gratis en Compras sobre $20", "Aplica a toda la zona dentro de 3km", 15, "ENVIODONSANTIAGO", "Siempre activo", false)
            ),
            menuItems = listOf(
                MenuItem("m_21", "biz_bodega_1", "Pack Selección Vinos & Tabla de Quesos", "Vino Malbec Reserva 750ml + Queso madurado 250g + Galletas finas", 65.0, "Gourmet", true, badge = "Pack Ahorro"),
                MenuItem("m_22", "biz_bodega_1", "Café de Especialidad Grano Entero 500g", "Tostado medio origen Villa Rica, notas a chocolate y avellanas", 34.0, "Abarrotes", true),
                MenuItem("m_23", "biz_bodega_1", "Aceite de Oliva Extra Virgen 500ml", "Prensado en frío de primera extracción", 28.0, "Abarrotes", true),
                MenuItem("m_24", "biz_bodega_1", "Canasta Básica Premium Familiar", "Arroz superior 5kg, aceite, leche evaporada pack x6, avena y miel", 55.0, "Básicos", true, 64.0)
            )
        ),
        Business(
            id = "biz_cafe_1",
            name = "Origen & Tueste Café Boutique",
            category = BusinessCategory.CAFETERIA,
            description = "Café de especialidad de fincas sostenibles. Métodos de extracción filtrados (V60, Chemex, Aeropress) y repostería artesanal en un ambiente sereno.",
            rating = 4.9,
            reviewCount = 310,
            address = "Av. Santa Cruz 890",
            latitude = -12.1170,
            longitude = -77.0340,
            phone = "+51 944 332 211",
            openingHours = "Lun - Dom: 07:30 - 21:30",
            isOpen = true,
            isVisibleInApp = true,
            verificationStatus = VerificationStatus.VERIFICADO_PUBLICADO,
            reputationScore = 99,
            reputationVerdict = "Excelencia Operativa y Trazabilidad",
            verifiedByAdminDate = "18 Oct 2026",
            ownerName = "Mariana Falconí",
            ownerEmail = "mariana@origenytueste.com",
            documents = listOf(
                VerificationDocument("doc_31", DocumentType.RUC_TRIBUTARIO, "20601245678", "RUC_OrigenCafe.pdf", "16 Oct 2026", true, "Tributos al día"),
                VerificationDocument("doc_32", DocumentType.LICENCIA_MUNICIPAL, "LIC-5002-2022", "Licencia_Cafeteria.pdf", "16 Oct 2026", true, "Zona comercial con terraza"),
                VerificationDocument("doc_33", DocumentType.CERTIFICADO_SANITARIO, "DIGESA-SAN-44", "Certificado_Manipulacion_Cafe.pdf", "17 Oct 2026", true, "Baristas certificados en higiene"),
                VerificationDocument("doc_34", DocumentType.DNI_REPRESENTANTE, "48102938", "DNI_Mariana_Falconi.pdf", "16 Oct 2026", true, "Titular conforme")
            ),
            promotions = listOf(
                Promotion("p_4", "biz_cafe_1", "Combo Desayuno Signature 25% OFF", "Café latte + tostón de palta y huevo pochado", 25, "DESAYUNO25", "Hasta las 11:30 AM", true)
            ),
            menuItems = listOf(
                MenuItem("m_31", "biz_cafe_1", "Flat White Doble Origen Cuzco", "Espresso doble con leche texturizada microespuma sedosa", 12.0, "Bebidas Calientes", true, badge = "Favorito"),
                MenuItem("m_32", "biz_cafe_1", "Cold Brew Infusionado 18 Horas", "Extracción en frío de 18 horas con notas cítricas refrescantes", 14.0, "Bebidas Frías", true),
                MenuItem("m_33", "biz_cafe_1", "Tostón de Palta Haas & Salmón Ahumado", "Pan de masa madre tostado, palta aliñada y brotes orgánicos", 24.0, "Brunch & Toasts", true),
                MenuItem("m_34", "biz_cafe_1", "Matcha Latte Ceremonial Orgánico Uji", "Matcha japonés grado ceremonial batido con leche de avena", 16.0, "Especiales", true)
            )
        ),
        Business(
            id = "biz_restaurant_1",
            name = "Fuego & Raíz Cocina de Autor",
            category = BusinessCategory.RESTAURANTE,
            description = "Cocina contemporánea a leña con insumos nativos y pesca del día. Una propuesta gastronómica de alto nivel en un espacio de diseño cálido y acogedor.",
            rating = 4.8,
            reviewCount = 412,
            address = "Calle Coronel Inclán 315",
            latitude = -12.1228,
            longitude = -77.0315,
            phone = "+51 955 667 788",
            openingHours = "Mar - Dom: 12:30 - 23:00",
            isOpen = false, // Temporarily closed (shows status control)
            isVisibleInApp = true,
            verificationStatus = VerificationStatus.VERIFICADO_PUBLICADO,
            reputationScore = 98,
            reputationVerdict = "Reconocimiento Gastronómico Oficial",
            verifiedByAdminDate = "25 Oct 2026",
            ownerName = "Chef Rodrigo Benavides",
            ownerEmail = "reservas@fuegoyraiz.pe",
            documents = listOf(
                VerificationDocument("doc_41", DocumentType.RUC_TRIBUTARIO, "20607788990", "Ficha_RUC_FuegoRaiz.pdf", "24 Oct 2026", true, "Auditoría contable aprobada"),
                VerificationDocument("doc_42", DocumentType.LICENCIA_MUNICIPAL, "LIC-9910-2023", "Licencia_Restaurante_Turistico.pdf", "24 Oct 2026", true, "Licencia restaurante categoría especial"),
                VerificationDocument("doc_43", DocumentType.CERTIFICADO_SANITARIO, "DIGESA-RE-882", "Inspeccion_HACCP_Inocuidad.pdf", "25 Oct 2026", true, "Plan HACCP implementado"),
                VerificationDocument("doc_44", DocumentType.DNI_REPRESENTANTE, "10293847", "DNI_Rodrigo_Benavides.pdf", "24 Oct 2026", true, "Titular verificado")
            ),
            promotions = listOf(
                Promotion("p_5", "biz_restaurant_1", "Cena Degustación de 4 Tiempos", "Maridaje incluido para 2 personas", 15, "FUEGOVIP", "Requiere reserva previa", false)
            ),
            menuItems = listOf(
                MenuItem("m_41", "biz_restaurant_1", "Bife Angosto a la Leña de Quebracho 350g", "Acompañado de papas nativas doradas y chimichurri de la casa", 68.0, "Carnes al Fuego", true, badge = "Chef Signature"),
                MenuItem("m_42", "biz_restaurant_1", "Arroz Meloso con Mariscos del Pacífico", "Conchas de abanico, pulpo y langostinos flambeados al pisco", 58.0, "Mar", true),
                MenuItem("m_43", "biz_restaurant_1", "Ravioles Rellenos de Ossobuco Braseado", "En su propio jugo glaseado con queso grana padano rallado", 52.0, "Pastas Artesanales", true)
            )
        ),
        // Pending Verification Business (Needs Admin Approval!)
        Business(
            id = "biz_pending_1",
            name = "Barbería & Spa Urbano Alpha King",
            category = BusinessCategory.BARBERIA,
            description = "Nueva barbería moderna orientada a cortes urbanos, degradados fade y spa facial masculino.",
            rating = 4.5,
            reviewCount = 12,
            address = "Av. Larco 820, Piso 2",
            latitude = -12.1260,
            longitude = -77.0285,
            phone = "+51 911 222 333",
            openingHours = "Lun - Sáb: 10:00 - 21:00",
            isOpen = true,
            isVisibleInApp = false, // Not yet approved by Admin!
            verificationStatus = VerificationStatus.PENDIENTE_REVISION,
            reputationScore = 88,
            reputationVerdict = "Documentación en proceso de auditoría",
            verifiedByAdminDate = "Pendiente de Validación",
            ownerName = "Kevin Zambrano",
            ownerEmail = "kevin.alpha@gmail.com",
            documents = listOf(
                VerificationDocument("doc_p1", DocumentType.RUC_TRIBUTARIO, "20609944123", "RUC_Alpha_King.pdf", "02 Nov 2026", false, "RUC nuevo en evaluación"),
                VerificationDocument("doc_p2", DocumentType.LICENCIA_MUNICIPAL, "TRAMITE-8812", "Constancia_Tramite_Licencia.pdf", "02 Nov 2026", false, "Constancia de inspección pendiente"),
                VerificationDocument("doc_p3", DocumentType.DNI_REPRESENTANTE, "72991044", "DNI_Kevin_Zambrano.pdf", "02 Nov 2026", true, "DNI validado en RENIEC")
            ),
            promotions = emptyList(),
            menuItems = listOf(
                MenuItem("m_p1", "biz_pending_1", "Corte Urbano Fade + Cejas", "Corte desvanecido milimétrico con navaja", 35.0, "Cortes", true)
            )
        ),
        // International Verified Businesses for World Map Access
        Business(
            id = "biz_madrid_1",
            name = "Gran Vía Classic Barber",
            category = BusinessCategory.BARBERIA,
            description = "Barbería clásica madrileña en el corazón de Gran Vía.",
            rating = 4.9,
            reviewCount = 520,
            address = "Calle Gran Vía 42, Madrid, España",
            latitude = 40.4200,
            longitude = -3.7050,
            phone = "+34 91 555 1234",
            openingHours = "Lun - Sáb: 10:00 - 21:00",
            isOpen = true,
            isVisibleInApp = true,
            verificationStatus = VerificationStatus.VERIFICADO_PUBLICADO,
            reputationScore = 99,
            reputationVerdict = "Certificado Comunidad de Madrid",
            menuItems = listOf(
                MenuItem("m_mad_1", "biz_madrid_1", "Afeitado Clásico a Navaja", "Con toallas aromáticas calientes", 28.0, "Barbería", true)
            )
        ),
        Business(
            id = "biz_madrid_2",
            name = "Chocolatería & Repostería Imperial",
            category = BusinessCategory.REPOSTERIA,
            description = "Repostería artesanal y churros tradicionales desde 1894.",
            rating = 4.8,
            reviewCount = 980,
            address = "Pasadizo de San Ginés 5, Madrid",
            latitude = 40.4168,
            longitude = -3.7070,
            phone = "+34 91 365 6546",
            openingHours = "Lun - Dom: 08:00 - 23:30",
            isOpen = true,
            isVisibleInApp = true,
            verificationStatus = VerificationStatus.VERIFICADO_PUBLICADO,
            reputationScore = 98,
            reputationVerdict = "Patrimonio Gastronómico Verificado",
            menuItems = listOf(
                MenuItem("m_mad_2", "biz_madrid_2", "Ración de Churros con Chocolate Caliente", "Elaborado con cacao puro", 8.5, "Postres", true)
            )
        ),
        Business(
            id = "biz_nyc_1",
            name = "SoHo Artisan Pastry & Bakery",
            category = BusinessCategory.REPOSTERIA,
            description = "French pastries and organic sourdough breads baked daily in SoHo.",
            rating = 4.9,
            reviewCount = 740,
            address = "Spring St & Mercer St, New York, NY",
            latitude = 40.7230,
            longitude = -74.0000,
            phone = "+1 212 555 0192",
            openingHours = "Mon - Sun: 07:00 - 20:00",
            isOpen = true,
            isVisibleInApp = true,
            verificationStatus = VerificationStatus.VERIFICADO_PUBLICADO,
            reputationScore = 99,
            reputationVerdict = "NYC Health Dept Grade A Certified",
            menuItems = listOf(
                MenuItem("m_nyc_1", "biz_nyc_1", "Cronut Signature & Latte", "Flaky layered pastry", 12.0, "Bakery", true)
            )
        ),
        Business(
            id = "biz_cdmx_1",
            name = "Repostería & Café Roma Norte",
            category = BusinessCategory.REPOSTERIA,
            description = "Panadería artesanal y repostería de autor en la mítica Colonia Roma.",
            rating = 4.9,
            reviewCount = 610,
            address = "Colima 166, Roma Norte, Ciudad de México",
            latitude = 19.4210,
            longitude = -99.1605,
            phone = "+52 55 5533 7804",
            openingHours = "Mar - Dom: 08:00 - 21:00",
            isOpen = true,
            isVisibleInApp = true,
            verificationStatus = VerificationStatus.VERIFICADO_PUBLICADO,
            reputationScore = 98,
            reputationVerdict = "Verificación Sanitaria y Comercial CDMX",
            menuItems = listOf(
                MenuItem("m_cdmx_1", "biz_cdmx_1", "Rol de Guayaba & Queso Ricotta", "Especialidad horneada a la leña", 14.0, "Panadería", true)
            )
        )
    )

    // Initial Job Offers for Youth (No experience required!)
    private val initialJobOffers = listOf(
        JobOffer(
            id = "job_1",
            businessId = "biz_reposteria_1",
            businessName = "Dulce Atelier Repostería Fina",
            businessCategory = BusinessCategory.REPOSTERIA,
            title = "Ayudante de Repostería y Empaque Gourmet",
            description = "¿Te apasiona la pastelería y buscas tu primera oportunidad laboral? En Dulce Atelier buscamos jóvenes entusiastas con deseos de aprender el arte de la repostería fina. Te capacitamos desde cero en mise en place, pesado de ingredientes y armado de pedidos.",
            isYouthNoExperience = true,
            employmentType = "Medio Tiempo (Tardes: 2:00 PM - 7:30 PM)",
            salaryMonthly = "S/ 850 + Bonos de productividad",
            workSchedule = "Lunes a Viernes (Fines de semana libres para estudiar)",
            address = "Calle Los Sauces 182, San Isidro",
            openingsCount = 2,
            requirements = listOf(
                "No se requiere experiencia previa (¡Te capacitamos nosotros!)",
                "Edad recomendada: 18 a 26 años",
                "Puntualidad, ganas de superación y actitud proactiva",
                "Cuidado por la limpieza y atención al detalle"
            ),
            benefits = listOf(
                "Capacitación pagada en técnicas de pastelería europea",
                "Descuentos de personal del 40% en toda la tienda",
                "Certificado de trabajo y constancia de prácticas",
                "Excelente clima laboral joven y colaborativo"
            )
        ),
        JobOffer(
            id = "job_2",
            businessId = "biz_cafe_1",
            businessName = "Origen & Tueste Café Boutique",
            businessCategory = BusinessCategory.CAFETERIA,
            title = "Barista Aprendiz & Atención en Barra",
            description = "Aprende el fascinante mundo del café de especialidad. Buscamos jóvenes con chispa, sonrisa y carisma para entrenarlos en servicio al cliente, preparación de bebidas con espresso y métodos artesanales.",
            isYouthNoExperience = true,
            employmentType = "Medio Tiempo (Turno Mañana: 7:30 AM - 1:00 PM)",
            salaryMonthly = "S/ 900 + Propinas semanales",
            workSchedule = "Martes a Sábado (Ideal para estudiantes de tarde/noche)",
            address = "Av. Santa Cruz 890, Miraflores",
            openingsCount = 3,
            requirements = listOf(
                "Sin experiencia previa requerida",
                "Ganas reales de aprender calibración de café y barismo",
                "Excelente trato cordial y empatía con clientes",
                "Responsabilidad y puntualidad"
            ),
            benefits = listOf(
                "Curso integral de Barismo Básico e Intermedio certificado",
                "Café ilimitado durante tu jornada laboral",
                "Línea de carrera a Barista Senior o Encargado de Barra",
                "Ingreso directo a planilla con beneficios de ley (MYPE)"
            )
        ),
        JobOffer(
            id = "job_3",
            businessId = "biz_barber_1",
            businessName = "Imperio Real Barber Studio",
            businessCategory = BusinessCategory.BARBERIA,
            title = "Asistente de Salón, Recepción & Barber Junior",
            description = "Oportunidad para jóvenes que quieran integrarse al sector de barbería y cuidado personal. Apoyo en recepción de clientes, agenda de turnos por WhatsApp/App, mantenimiento de estaciones de trabajo y aprendizaje de técnicas con maestros barberos.",
            isYouthNoExperience = true,
            employmentType = "Fines de Semana (Viernes, Sábados y Domingos)",
            salaryMonthly = "S/ 650 + Comisiones por venta de productos",
            workSchedule = "Viernes 2 PM - 8 PM, Sábados 10 AM - 7 PM, Domingos 10 AM - 5 PM",
            address = "Av. Del Parque 450, Miraflores",
            openingsCount = 2,
            requirements = listOf(
                "100% Sin experiencia (te entrenamos en atención y estilismo)",
                "Buena presencia, carisma y orientación de servicio",
                "Manejo fluido de celular y redes sociales",
                "Compromiso y ganas de crecer"
            ),
            benefits = listOf(
                "Mentoría directa con barberos profesionales con años de experiencia",
                "Cortes y arreglos de barba 100% gratuitos para ti",
                "Oportunidad de pasar a Barbero Titular al culminar entrenamiento",
                "Ambiente dinámico y juvenil"
            )
        ),
        JobOffer(
            id = "job_4",
            businessId = "biz_bodega_1",
            businessName = "Bodega Gourmet Don Santiago",
            businessCategory = BusinessCategory.BODEGA,
            title = "Auxiliar de Reposición, Caja & Delivery Local",
            description = "Apoyo en recepción de mercadería, armado de pedidos de la aplicación y atención en caja registradora moderna. Si tienes bicicleta o te gusta caminar en la zona cercana, ¡esta es tu oportunidad!",
            isYouthNoExperience = true,
            employmentType = "Tiempo Completo o Medio Tiempo Rotativo",
            salaryMonthly = "S/ 1,100 + Bono de puntualidad S/ 150",
            workSchedule = "Lunes a Viernes con 1 día de descanso adicional",
            address = "Paseo de la República 1102",
            openingsCount = 2,
            requirements = listOf(
                "No requiere experiencia laboral",
                "Mayor de 18 años con DNI vigente",
                "Facilidad para comunicarse y actitud positiva",
                "Conocimiento básico de operaciones aritméticas en caja"
            ),
            benefits = listOf(
                "Pagos puntuales quincenales garantizados",
                "Almuerzo cubierto por el local",
                "Seguro integral de salud",
                "Flexibilidad para exámenes universitarios o de instituto"
            )
        )
    )

    // Initial Notifications
    private val initialNotifications = listOf(
        AppNotification(
            id = "notif_1",
            type = NotificationType.OFERTA_EXCLUSIVA,
            title = "🎉 ¡Promo Exclusiva Cerca de Ti!",
            message = "Dulce Atelier activó un 2x1 en porciones de Cheesecake Vasco por las próximas 3 horas.",
            timestamp = "Hace 15 min",
            relatedBusinessId = "biz_reposteria_1"
        ),
        AppNotification(
            id = "notif_2",
            type = NotificationType.ESTADO_NEGOCIO,
            title = "🛡️ Negocio Verificado por Admin",
            message = "Origen & Tueste Café Boutique superó la auditoría de reputación con puntaje 99/100.",
            timestamp = "Hace 1 hora",
            relatedBusinessId = "biz_cafe_1"
        ),
        AppNotification(
            id = "notif_3",
            type = NotificationType.EMPLEO_JOVEN,
            title = "💼 Nueva Oferta: Sin Experiencia Previa",
            message = "Dulce Atelier busca 2 ayudantes de repostería joven con capacitación pagada. ¡Postula en 1 clic!",
            timestamp = "Hace 2 horas",
            relatedBusinessId = "biz_reposteria_1"
        ),
        AppNotification(
            id = "notif_4",
            type = NotificationType.SISTEMA_ADMIN,
            title = "🛡️ Seguridad y Verificación Urbe",
            message = "Recuerda que todos los negocios visibles cuentan con RUC y Licencia municipal validados por nuestro equipo.",
            timestamp = "Hoy",
            relatedBusinessId = null
        )
    )

    // State Flows
    private val _businesses = MutableStateFlow(initialBusinesses)
    val businesses: StateFlow<List<Business>> = _businesses.asStateFlow()

    private val _jobOffers = MutableStateFlow(initialJobOffers)
    val jobOffers: StateFlow<List<JobOffer>> = _jobOffers.asStateFlow()

    private val _jobApplications = MutableStateFlow<List<JobApplication>>(listOf(
        JobApplication(
            id = "app_demo_1",
            jobOfferId = "job_2",
            jobTitle = "Barista Aprendiz & Atención en Barra",
            businessName = "Origen & Tueste Café Boutique",
            candidateName = "Mateo Silva",
            candidateAge = 20,
            candidatePhone = "+51 988 776 655",
            candidateBio = "Estudiante de tercer ciclo. Me encanta el café, soy muy puntual y busco mi primer empleo formal.",
            availability = "Turno Mañana (7:30 AM - 1:00 PM)",
            skills = listOf("Puntualidad", "Atención al Cliente", "Trabajo en Equipo"),
            status = JobApplicationStatus.EN_REVISION,
            appliedDate = "04 Nov 2026",
            businessNotes = "Perfil con muy buena disposición. Revisando disponibilidad horaria."
        )
    ))
    val jobApplications: StateFlow<List<JobApplication>> = _jobApplications.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _notifications = MutableStateFlow(initialNotifications)
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _cartItems = MutableStateFlow<List<OrderItem>>(emptyList())
    val cartItems: StateFlow<List<OrderItem>> = _cartItems.asStateFlow()

    private val _cartBusinessId = MutableStateFlow<String?>(null)
    val cartBusinessId: StateFlow<String?> = _cartBusinessId.asStateFlow()

    private val _userRoleMode = MutableStateFlow(UserRoleMode.EXPLORER)
    val userRoleMode: StateFlow<UserRoleMode> = _userRoleMode.asStateFlow()

    // World Metropolises for Global Exploration Mode
    val worldCities = listOf(
        WorldCity("city_lima", "Lima", "Perú", "🇵🇪", -12.1210, -77.0300, "Tu Ubicación Actual • Miraflores & San Isidro", isUserLocation = true),
        WorldCity("city_madrid", "Madrid", "España", "🇪🇸", 40.4168, -3.7038, "Gran Vía, Malasaña y Salamanca"),
        WorldCity("city_cdmx", "Ciudad de México", "México", "🇲🇽", 19.4326, -99.1332, "Roma Norte, Condesa y Polanco"),
        WorldCity("city_nyc", "Nueva York", "EE.UU.", "🇺🇸", 40.7128, -74.0060, "Manhattan, Soho & Brooklyn"),
        WorldCity("city_ba", "Buenos Aires", "Argentina", "🇦🇷", -34.6037, -58.3816, "Palermo Soho y Recoleta"),
        WorldCity("city_paris", "París", "Francia", "🇫🇷", 48.8566, 2.3522, "Le Marais y Saint-Germain"),
        WorldCity("city_tokyo", "Tokio", "Japón", "🇯🇵", 35.6762, 139.6503, "Shibuya, Ginza y Shinjuku")
    )

    // Map Guides / POIs
    val mapPoiGuides = listOf(
        MapPoiGuide("poi_1", "Parque Metropolitano Central", "Espacio Verde & Recreativo", "Zona arbolada con ciclovías y ferias locales", -12.1200, -77.0305, "park"),
        MapPoiGuide("poi_2", "Boulevard Gastronómico Miraflores", "Corredor Comercial", "Paseo peatonal con restaurantes y cafés selectos", -12.1220, -77.0320, "local_dining"),
        MapPoiGuide("poi_3", "Plaza de Armas & Catedral", "Centro Cívico Histórico", "Punto neurálgico con arquitectura colonial", -12.1150, -77.0280, "account_balance"),
        MapPoiGuide("poi_4", "Eje Financiero & Empresarial", "Zona Corporativa", "Concentración de oficinas y servicios bancarios", -12.1250, -77.0330, "domain"),
        MapPoiGuide("poi_5", "Malecón de la Reserva & Faro", "Mirador Panorámico", "Acantilado con vista oceánica y ciclovía panorámica", -12.1245, -77.0360, "beach_access")
    )

    // User Location (Simulated GPS Coordinates for Miraflores/San Isidro zone)
    private val _currentUserLocation = MutableStateFlow(MapPoint(-12.1210, -77.0300, "Mi Ubicación (Parque Kennedy, Miraflores)"))
    val currentUserLocation: MapPoint get() = _currentUserLocation.value
    val currentUserLocationFlow: StateFlow<MapPoint> = _currentUserLocation.asStateFlow()

    // ADMIN ACTIONS: Document Verification & Publication
    fun approveBusinessVerification(businessId: String, reputationScore: Int = 98, adminVerdict: String = "Documentos y Reputación Aprobados por Admin") {
        _businesses.value = _businesses.value.map { biz ->
            if (biz.id == businessId) {
                biz.copy(
                    verificationStatus = VerificationStatus.VERIFICADO_PUBLICADO,
                    isVisibleInApp = true,
                    reputationScore = reputationScore,
                    reputationVerdict = adminVerdict,
                    verifiedByAdminDate = "05 Nov 2026",
                    documents = biz.documents.map { it.copy(isApproved = true, verificationNotes = "Validado oficialmente") }
                )
            } else biz
        }
        // Send a notification to public users about a newly verified business
        val approvedBiz = _businesses.value.firstOrNull { it.id == businessId }
        approvedBiz?.let {
            sendNotification(
                NotificationType.ESTADO_NEGOCIO,
                "✅ Nuevo Negocio Verificado: ${it.name}",
                "El Administrador ha validado los documentos legales y reputación comercial de ${it.name}. ¡Ya disponible en el mapa!",
                it.id
            )
        }
    }

    fun rejectBusinessVerification(businessId: String, reason: String) {
        _businesses.value = _businesses.value.map { biz ->
            if (biz.id == businessId) {
                biz.copy(
                    verificationStatus = VerificationStatus.RECHAZADO,
                    isVisibleInApp = false,
                    reputationVerdict = "Rechazado: $reason"
                )
            } else biz
        }
    }

    fun toggleBusinessVisibility(businessId: String) {
        _businesses.value = _businesses.value.map { biz ->
            if (biz.id == businessId) {
                val newVisibility = !biz.isVisibleInApp
                biz.copy(isVisibleInApp = newVisibility)
            } else biz
        }
    }

    fun toggleBusinessOpenClose(businessId: String) {
        _businesses.value = _businesses.value.map { biz ->
            if (biz.id == businessId) {
                val newOpenState = !biz.isOpen
                // Notify users if business opened
                if (newOpenState) {
                    sendNotification(
                        NotificationType.ESTADO_NEGOCIO,
                        "🟢 ¡${biz.name} abrió sus puertas!",
                        "El negocio está abierto y atendiendo pedidos ahora mismo.",
                        biz.id
                    )
                }
                biz.copy(isOpen = newOpenState)
            } else biz
        }
    }

    fun addNewBusinessByAdmin(newBiz: Business) {
        _businesses.value = listOf(newBiz) + _businesses.value
        if (newBiz.isEligibleForPublicDisplay) {
            sendNotification(
                NotificationType.ESTADO_NEGOCIO,
                "✨ Bienvenido a la plataforma: ${newBiz.name}",
                "Negocio verificado y publicado por la Administración.",
                newBiz.id
            )
        }
    }

    fun updateBusiness(updatedBiz: Business) {
        _businesses.value = _businesses.value.map { biz ->
            if (biz.id == updatedBiz.id) updatedBiz else biz
        }
        sendNotification(
            NotificationType.ESTADO_NEGOCIO,
            "📝 Negocio Actualizado: ${updatedBiz.name}",
            "La información comercial, precios, ofertas y fotos de ${updatedBiz.name} han sido actualizados en tiempo real.",
            updatedBiz.id
        )
    }

    fun updateUserLocation(lat: Double, lng: Double, label: String = "Mi Ubicación GPS Actual") {
        _currentUserLocation.value = MapPoint(
            lat = lat,
            lng = lng,
            name = label
        )
    }

    // MENU & PROMOTION MANAGEMENT
    fun addMenuItem(businessId: String, item: MenuItem) {
        _businesses.value = _businesses.value.map { biz ->
            if (biz.id == businessId) {
                biz.copy(menuItems = biz.menuItems + item)
            } else biz
        }
    }

    fun toggleMenuItemAvailability(businessId: String, itemId: String) {
        _businesses.value = _businesses.value.map { biz ->
            if (biz.id == businessId) {
                biz.copy(menuItems = biz.menuItems.map {
                    if (it.id == itemId) it.copy(isAvailable = !it.isAvailable) else it
                })
            } else biz
        }
    }

    fun addPromotion(businessId: String, promotion: Promotion) {
        _businesses.value = _businesses.value.map { biz ->
            if (biz.id == businessId) {
                biz.copy(promotions = biz.promotions + promotion)
            } else biz
        }
        val biz = _businesses.value.firstOrNull { it.id == businessId }
        biz?.let {
            sendNotification(
                NotificationType.OFERTA_EXCLUSIVA,
                "🔥 Nueva Promo en ${it.name}: ${promotion.title}",
                "${promotion.description}. Código: ${promotion.promoCode}",
                it.id
            )
        }
    }

    // CART & COMMERCE
    fun addToCart(item: MenuItem, businessId: String) {
        // If switching business, reset cart
        if (_cartBusinessId.value != null && _cartBusinessId.value != businessId) {
            _cartItems.value = emptyList()
        }
        _cartBusinessId.value = businessId

        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.menuItem.id == item.id }
        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            currentList[existingIndex] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentList.add(OrderItem(menuItem = item, quantity = 1))
        }
        _cartItems.value = currentList
    }

    fun removeFromCart(itemId: String) {
        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.menuItem.id == itemId }
        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            if (existing.quantity > 1) {
                currentList[existingIndex] = existing.copy(quantity = existing.quantity - 1)
            } else {
                currentList.removeAt(existingIndex)
            }
        }
        _cartItems.value = currentList
        if (currentList.isEmpty()) {
            _cartBusinessId.value = null
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
        _cartBusinessId.value = null
    }

    fun placeOrder(
        paymentMethod: PaymentMethod,
        deliveryAddress: String,
        customerNotes: String
    ): Order? {
        val items = _cartItems.value
        val bizId = _cartBusinessId.value ?: return null
        val biz = _businesses.value.firstOrNull { it.id == bizId } ?: return null

        val subtotal = items.sumOf { it.lineTotal }
        val deliveryFee = 5.0
        val discount = if (biz.promotions.isNotEmpty()) subtotal * (biz.promotions.first().discountPercent / 100.0) else 0.0
        val total = (subtotal + deliveryFee - discount).coerceAtLeast(0.0)

        val newOrder = Order(
            id = "ORD-${System.currentTimeMillis() % 100000}",
            businessId = bizId,
            businessName = biz.name,
            items = items,
            subtotal = subtotal,
            deliveryFee = deliveryFee,
            discount = discount,
            total = total,
            paymentMethod = paymentMethod,
            status = OrderStatus.RECIBIDO,
            deliveryAddress = deliveryAddress,
            createdAt = "Hoy, Hace un momento",
            customerNotes = customerNotes
        )

        _orders.value = listOf(newOrder) + _orders.value
        clearCart()

        // Send confirmation notification
        sendNotification(
            NotificationType.PEDIDO_ACTUALIZACION,
            "📦 Pedido Confirmado: #${newOrder.id}",
            "Tu orden en ${biz.name} por S/ ${String.format("%.2f", total)} fue procesada exitosamente mediante ${paymentMethod.label}.",
            biz.id
        )

        return newOrder
    }

    fun advanceOrderStatus(orderId: String) {
        _orders.value = _orders.value.map { order ->
            if (order.id == orderId) {
                val nextStatus = when (order.status) {
                    OrderStatus.RECIBIDO -> OrderStatus.EN_PREPARACION
                    OrderStatus.EN_PREPARACION -> OrderStatus.EN_CAMINO
                    OrderStatus.EN_CAMINO -> OrderStatus.ENTREGADO
                    OrderStatus.ENTREGADO -> OrderStatus.ENTREGADO
                }
                sendNotification(
                    NotificationType.PEDIDO_ACTUALIZACION,
                    "🚚 Actualización de Pedido #${order.id}",
                    "Estado actualizado: ${nextStatus.title} - ${nextStatus.subtitle}",
                    order.businessId
                )
                order.copy(status = nextStatus)
            } else order
        }
    }

    // YOUTH JOB OFFERS & APPLICATIONS
    fun postJobOffer(jobOffer: JobOffer) {
        _jobOffers.value = listOf(jobOffer) + _jobOffers.value
        sendNotification(
            NotificationType.EMPLEO_JOVEN,
            "💼 Nueva Oferta Juvenil en ${jobOffer.businessName}",
            "${jobOffer.title} (${jobOffer.employmentType}). ¡Ideal para tu primer empleo!",
            jobOffer.businessId
        )
    }

    fun applyForJob(
        jobOfferId: String,
        applicantName: String,
        applicantAge: Int,
        applicantPhone: String,
        applicantBio: String,
        availability: String,
        skills: List<String>
    ): JobApplication? {
        val job = _jobOffers.value.firstOrNull { it.id == jobOfferId } ?: return null

        val application = JobApplication(
            id = "app_${UUID.randomUUID().toString().take(8)}",
            jobOfferId = jobOfferId,
            jobTitle = job.title,
            businessName = job.businessName,
            candidateName = applicantName,
            candidateAge = applicantAge,
            candidatePhone = applicantPhone,
            candidateBio = applicantBio,
            availability = availability,
            skills = skills,
            status = JobApplicationStatus.POSTULADO,
            appliedDate = "Hoy",
            businessNotes = "Postulación recibida en Urbe Empleo Joven."
        )

        _jobApplications.value = listOf(application) + _jobApplications.value

        sendNotification(
            NotificationType.EMPLEO_JOVEN,
            "✅ Postulación Enviada con Éxito",
            "Te has postulado a '${job.title}' en ${job.businessName}. El negocio revisará tu perfil.",
            job.businessId
        )

        return application
    }

    // NOTIFICATIONS
    fun sendNotification(type: NotificationType, title: String, message: String, businessId: String? = null) {
        val notif = AppNotification(
            id = "notif_${System.currentTimeMillis()}",
            type = type,
            title = title,
            message = message,
            timestamp = "Ahora",
            isRead = false,
            relatedBusinessId = businessId
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    fun markNotificationAsRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsAsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    // USER ROLE SWITCHING
    fun setUserRoleMode(mode: UserRoleMode) {
        _userRoleMode.value = mode
    }
}
