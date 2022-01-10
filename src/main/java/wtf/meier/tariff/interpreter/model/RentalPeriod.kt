package wtf.meier.tariff.interpreter.model

import wtf.meier.tariff.interpreter.extension.minus
import wtf.meier.tariff.interpreter.model.goodwill.ChargedGoodwill
import java.time.Instant

data class RentalPeriod(
    val rentalStart: Instant = Instant.ofEpochMilli(0),
    val rentalEnd: Instant,
    val invoicedStart: Instant = rentalStart,
    val invoicedEnd: Instant = rentalEnd,
    val chargedGoodwill: ChargedGoodwill? = null
) {
    val duration: Instant
        get() = invoicedEnd - invoicedStart
}
