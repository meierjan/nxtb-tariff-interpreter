package wtf.meier.tariff.interpreter.model.goodwill.calculator

import wtf.meier.tariff.interpreter.extension.plus
import wtf.meier.tariff.interpreter.extension.minOf
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.FreeMinutes
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import java.util.*

object FreeMinutesCalculator {
    fun calculateGoodwill(freeMinutes: FreeMinutes, rentalPeriod: RentalPeriod, currency: Currency): RentalPeriod {
        val calculatedGoodwill = minOf(rentalPeriod.duration , freeMinutes.duration)
        val freeMinutesPosition = RateCalculator.CalculatedPrice(
            price = Price(0),
            currency = currency,
            calculationStart = rentalPeriod.calculatedStart,
            calculationEnd = rentalPeriod.calculatedStart + calculatedGoodwill,
            description = "${calculatedGoodwill.timeUnit.toMinutes(calculatedGoodwill.timeAmount.toLong())} minutes were deducted from your travel time as a gesture of goodwill"
        )

        rentalPeriod.positions.add(freeMinutesPosition)

        return rentalPeriod.copy(calculatedStart = rentalPeriod.calculatedStart + calculatedGoodwill)
    }
}
