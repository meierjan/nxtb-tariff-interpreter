package wtf.meier.tariff.interpreter.model

import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.extension.minus
import java.time.Instant

data class RentalPeriod(
    val rentalStart: Instant = Instant.ofEpochMilli(0),
    val rentalEnd: Instant,
    val calculatedStart: Instant = rentalStart,
    val calculatedEnd: Instant = rentalEnd,
    val positions: MutableList<RateCalculator.CalculatedPrice> = mutableListOf()
) {
    val duration:Instant
        get() = calculatedEnd - calculatedStart
}
