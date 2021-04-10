package wtf.meier.tariff.interpreter.model

import java.util.*

data class Receipt(
    val price: Int,
    val currency: Currency,
    val log: List<String> = emptyList()
)