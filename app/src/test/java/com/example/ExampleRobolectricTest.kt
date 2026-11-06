package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.UrbeRepository
import com.example.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Urbe", appName)
    }

    @Test
    fun `admin verifies and publishes business in real-time`() {
        val repository = UrbeRepository()
        val pending = repository.businesses.value.firstOrNull { it.id == "biz_pending_1" }
        assertNotNull(pending)
        assertEquals(false, pending?.isVisibleInApp)
        assertEquals(VerificationStatus.PENDIENTE_REVISION, pending?.verificationStatus)

        // Admin approves documents and validates reputation
        repository.approveBusinessVerification("biz_pending_1", 98, "Auditoría Aprobada")

        val approved = repository.businesses.value.firstOrNull { it.id == "biz_pending_1" }
        assertNotNull(approved)
        assertEquals(true, approved?.isVisibleInApp)
        assertEquals(VerificationStatus.VERIFICADO_PUBLICADO, approved?.verificationStatus)
        assertEquals(98, approved?.reputationScore)
        assertTrue(approved?.isEligibleForPublicDisplay == true)
    }

    @Test
    fun `order creation and checkout processes successfully`() {
        val repository = UrbeRepository()
        val sampleItem = MenuItem("m_test_1", "biz_barber_1", "Servicio Test", "Desc", 45.0, "Cortes")
        repository.addToCart(sampleItem, "biz_barber_1")

        assertEquals(1, repository.cartItems.value.size)

        val order = repository.placeOrder(
            paymentMethod = PaymentMethod.TARJETA,
            deliveryAddress = "Calle Test 123",
            customerNotes = "Sin timbre"
        )

        assertNotNull(order)
        assertEquals(0, repository.cartItems.value.size)
        assertEquals(1, repository.orders.value.size)
        assertEquals(OrderStatus.RECIBIDO, order?.status)
    }

    @Test
    fun `youth job application registers from personal profile`() {
        val repository = UrbeRepository()
        val job = repository.jobOffers.value.first()

        val app = repository.applyForJob(
            jobOfferId = job.id,
            applicantName = "Mateo Silva",
            applicantAge = 20,
            applicantPhone = "+51 988 776 655",
            applicantBio = "Primer empleo con muchas ganas de aprender.",
            availability = "Medio Tiempo",
            skills = listOf("Puntualidad", "Atención al cliente")
        )

        assertNotNull(app)
        assertEquals(JobApplicationStatus.POSTULADO, app?.status)
        assertTrue(repository.jobApplications.value.any { it.jobOfferId == job.id })
    }

    @Test
    fun `create and save new business with all specifications appears on map`() {
        val repository = UrbeRepository()
        val initialCount = repository.businesses.value.size

        val newBiz = Business(
            id = "biz_custom_100",
            name = "Panadería Artesanal Don Pancho",
            category = BusinessCategory.REPOSTERIA,
            description = "Panadería de masa madre y pasteles caseros.",
            rating = 5.0,
            reviewCount = 1,
            address = "Av. José Larco 900",
            latitude = -12.1230,
            longitude = -77.0290,
            phone = "+51 987 112 233",
            openingHours = "07:00 - 21:00",
            isOpen = true,
            isVisibleInApp = true,
            verificationStatus = VerificationStatus.VERIFICADO_PUBLICADO,
            reputationScore = 98,
            menuItems = listOf(
                MenuItem("m_custom_1", "biz_custom_100", "Pan de Masa Madre", "Fermentación 24h", 12.0, "Panadería")
            ),
            promotions = listOf(
                Promotion("p_custom_1", "biz_custom_100", "20% OFF Inauguración", "Descuento en tu primera compra", 20, "PAN20", "Válido hoy")
            )
        )

        repository.addNewBusinessByAdmin(newBiz)

        assertEquals(initialCount + 1, repository.businesses.value.size)
        val saved = repository.businesses.value.firstOrNull { it.id == "biz_custom_100" }
        assertNotNull(saved)
        assertEquals("Panadería Artesanal Don Pancho", saved?.name)
        assertTrue(saved?.isEligibleForPublicDisplay == true)
        assertEquals(1, saved?.menuItems?.size)
        assertEquals(1, saved?.promotions?.size)
    }

    @Test
    fun `world exploration contains global metropolises`() {
        val repository = UrbeRepository()
        assertTrue(repository.worldCities.size >= 7)
        val lima = repository.worldCities.firstOrNull { it.isUserLocation }
        assertNotNull(lima)
        assertEquals("Lima", lima?.name)
        assertTrue(repository.worldCities.any { it.name == "Madrid" })
        assertTrue(repository.worldCities.any { it.name == "Nueva York" })
    }

    @Test
    fun `business updating modifies prices, website, promos and photos after creation`() {
        val repository = UrbeRepository()
        val originalBiz = repository.businesses.value.first { it.id == "biz_barber_1" }

        val updatedBiz = originalBiz.copy(
            websiteUrl = "https://www.emperadorbarbershop-luxury.pe",
            phone = "+51 999 888 777",
            coverPhotoUrl = "https://images.unsplash.com/photo-1503951914875",
            menuItems = originalBiz.menuItems.map { it.copy(price = it.price + 10.0) },
            promotions = listOf(
                Promotion("p_new_1", originalBiz.id, "30% OFF Fin de Semana", "Exclusivo", 30, "GOLD30", "Domingo")
            )
        )

        repository.updateBusiness(updatedBiz)

        val savedBiz = repository.businesses.value.first { it.id == "biz_barber_1" }
        assertEquals("https://www.emperadorbarbershop-luxury.pe", savedBiz.websiteUrl)
        assertEquals("+51 999 888 777", savedBiz.phone)
        assertEquals(originalBiz.menuItems.first().price + 10.0, savedBiz.menuItems.first().price, 0.01)
        assertEquals(1, savedBiz.promotions.size)
        assertEquals("GOLD30", savedBiz.promotions.first().promoCode)
    }

    @Test
    fun `payment methods support contraentrega, yape, transferencia and tarjeta`() {
        val repository = UrbeRepository()
        val item = repository.businesses.value.first().menuItems.first()
        repository.addToCart(item, item.businessId)

        // Test Yape / Billetera
        val yapeOrder = repository.placeOrder(
            paymentMethod = PaymentMethod.YAPE_BILLETERA,
            deliveryAddress = "Av. Larco 500",
            customerNotes = "Pago con Yape al 987-654-321"
        )
        assertNotNull(yapeOrder)
        assertEquals(PaymentMethod.YAPE_BILLETERA, yapeOrder?.paymentMethod)

        // Test Contraentrega / Efectivo
        repository.addToCart(item, item.businessId)
        val cashOrder = repository.placeOrder(
            paymentMethod = PaymentMethod.CONTRAENTREGA_EFECTIVO,
            deliveryAddress = "Calle Schell 240",
            customerNotes = "Pago contraentrega en efectivo, vuelto de S/ 100"
        )
        assertNotNull(cashOrder)
        assertEquals(PaymentMethod.CONTRAENTREGA_EFECTIVO, cashOrder?.paymentMethod)

        // Test Transferencia Bancaria
        repository.addToCart(item, item.businessId)
        val transferOrder = repository.placeOrder(
            paymentMethod = PaymentMethod.TRANSFERENCIA_BANCARIA,
            deliveryAddress = "Av. Benavides 123",
            customerNotes = "Transferencia directa BCP"
        )
        assertNotNull(transferOrder)
        assertEquals(PaymentMethod.TRANSFERENCIA_BANCARIA, transferOrder?.paymentMethod)
    }
}

