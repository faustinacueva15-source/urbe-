package com.example.model

enum class OrderStatus(val title: String, val subtitle: String, val stepIndex: Int) {
    RECIBIDO("Pedido Confirmado", "El local ha recibido y validado tu orden", 0),
    EN_PREPARACION("En Preparación", "El comercio está elaborando tus productos", 1),
    EN_CAMINO("En Camino", "Repartidor Urbe Express en ruta hacia tu dirección", 2),
    ENTREGADO("Entregado", "¡Disfruta tu pedido verificado!", 3)
}

enum class PaymentMethod(val label: String, val description: String, val instructions: String) {
    CONTRAENTREGA_EFECTIVO(
        label = "Pago Contra Entrega / Efectivo",
        description = "Paga en efectivo al repartidor al recibir tu pedido",
        instructions = "Ten a la mano el monto exacto o indica con cuánto pagarás para tu cambio."
    ),
    YAPE_BILLETERA(
        label = "Yape / Billetera Digital",
        description = "Transferencia instantánea sin comisiones vía QR o número",
        instructions = "Escanea el código QR o transfiere al número oficial del comercio y adjunta tu constancia."
    ),
    TRANSFERENCIA_BANCARIA(
        label = "Transferencia Bancaria Inmediata",
        description = "Depósito directo a cuenta BCP, BBVA o Interbank (CCI)",
        instructions = "Cta Cte BCP: 194-98218921-0-44 • CCI: 00219400982189210044"
    ),
    TARJETA(
        label = "Tarjeta de Crédito / Débito",
        description = "Procesamiento en línea protegido con cifrado SSL TLS 256-bit",
        instructions = "Aceptamos Visa, Mastercard, American Express y Diners Club."
    )
}

data class OrderItem(
    val menuItem: MenuItem,
    val quantity: Int,
    val specialNotes: String = ""
) {
    val lineTotal: Double
        get() = menuItem.price * quantity
}

data class Order(
    val id: String,
    val businessId: String,
    val businessName: String,
    val items: List<OrderItem>,
    val subtotal: Double,
    val deliveryFee: Double,
    val discount: Double,
    val total: Double,
    val paymentMethod: PaymentMethod,
    val status: OrderStatus = OrderStatus.RECIBIDO,
    val deliveryAddress: String,
    val createdAt: String,
    val estimatedArrival: String = "25 - 35 min",
    val customerNotes: String = ""
)
