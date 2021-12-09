package wtf.meier.tariff.interpreter.model

import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.extension.minus
import java.time.Instant
import java.util.concurrent.TimeUnit

data class RentalPeriod(
    val rentalStart: Instant = Instant.ofEpochMilli(0),
    val rentalEnd: Instant,
    val calculatedStart: Instant = rentalStart,
    val calculatedEnd: Instant = rentalEnd,
    val positions: MutableList<RateCalculator.CalculatedPrice> = mutableListOf()
) {
    val duration:Interval
        get() = Interval(calculatedEnd.toEpochMilli().toInt(), TimeUnit.MILLISECONDS) - Interval(calculatedStart.toEpochMilli().toInt(), TimeUnit.MILLISECONDS)
}
