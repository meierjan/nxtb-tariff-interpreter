package wtf.meier.tariff.interpreter.model.goodwill.calculator

import wtf.meier.tariff.interpreter.extension.minus
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.StaticGoodwill
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import java.util.*

object StaticGoodwillcalculator {
    fun calculateGoodwill(goodwill: StaticGoodwill, rentalPeriod: RentalPeriod, currency: Currency): RentalPeriod {
        val calculatedGoodwill =
            wtf.meier.tariff.interpreter.extension.minOf(rentalPeriod.duration, goodwill.duration)
        val freeMinutesPosition = RateCalculator.CalculatedPrice(
            price = Price(0),
            currency = currency,
            calculationStart = rentalPeriod.calculatedEnd - (calculatedGoodwill),
            calculationEnd = rentalPeriod.calculatedEnd,
            description = "${calculatedGoodwill.timeUnit.toMinutes(calculatedGoodwill.timeAmount.toLong())} minutes were deducted from your travel time as a gesture of goodwill"
        )

        rentalPeriod.positions.add(freeMinutesPosition)

        return rentalPeriod.copy(calculatedEnd = rentalPeriod.calculatedEnd - calculatedGoodwill)
    }
}