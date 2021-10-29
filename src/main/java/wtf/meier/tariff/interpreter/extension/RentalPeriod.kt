package wtf.meier.tariff.interpreter.extension

import wtf.meier.tariff.interpreter.Calculator
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import java.time.Instant

data class RentalPeriod(
    val rentalStart: Instant = Instant.ofEpochMilli(0),
    val rentalEnd: Instant,
    var calculatedStart: Instant = rentalStart,
    var calculatedEnd: Instant = rentalEnd,
    val positions: MutableList<RateCalculator.CalculatedPrice> = mutableListOf<RateCalculator.CalculatedPrice>()
){
    fun calculateDuration():Instant{
        return calculatedEnd - calculatedStart
    }
}
