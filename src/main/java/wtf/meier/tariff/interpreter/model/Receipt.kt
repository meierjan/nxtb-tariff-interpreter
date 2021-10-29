package wtf.meier.tariff.interpreter.model

import java.time.Instant
import java.util.*

data class Receipt(
    val positions: List<Position>,
    val currency: Currency,
) {

    val price: Int by lazy {
        positions.sumBy { it.price.credit }
    }
    // TODO make start and end non-optional

    data class Position(
        val price: Price,
        val description: String,
        val positionStart: Instant? = null,
        val positionEnd: Instant? = null
    )
}
