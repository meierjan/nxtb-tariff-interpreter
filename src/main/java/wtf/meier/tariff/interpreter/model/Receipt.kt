package wtf.meier.tariff.interpreter.model

import java.util.*

data class Receipt(
    val positions: List<Position>,
    val currency: Currency,
) {

    val price: Int by lazy {
        positions.sumBy { it.price.credit }
    }

    data class Position(
        val price: Price,
        val description: String
    )
}