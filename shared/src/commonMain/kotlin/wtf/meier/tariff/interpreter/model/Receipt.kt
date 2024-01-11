package wtf.meier.tariff.interpreter.model

import kotlinx.datetime.Instant

data class Receipt(
    val positions: List<Position>,
    val currency: Currency,
) {
    val price: Int by lazy {
        positions.sumOf { it.price?.credit ?: 0 }
    }

    data class Position(
        val price: Price? = null,
        val description: String,
        val positionStart: Instant,
        val positionEnd: Instant
    )
}
