package wtf.meier.tariff.interpreter.model.goodwill.calculator

import wtf.meier.tariff.interpreter.extension.minOf
import wtf.meier.tariff.interpreter.extension.plus
import wtf.meier.tariff.interpreter.extension.toInterval
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.ChargedGoodwill
import wtf.meier.tariff.interpreter.model.goodwill.FreeMinutes

object FreeMinutesCalculator {
    fun calculateGoodwill(freeMinutes: FreeMinutes, rentalPeriod: RentalPeriod): RentalPeriod {
        val calculatedGoodwill = minOf(rentalPeriod.duration.toInterval(), freeMinutes.duration)

        val chargedGoodwill = ChargedGoodwill(
            goodwillStart = rentalPeriod.invoicedStart,
            goodwillEnd = rentalPeriod.invoicedStart + calculatedGoodwill,
            description = "${calculatedGoodwill.timeUnit.toMinutes(calculatedGoodwill.timeAmount.toLong())} minutes were deducted from your travel time as a gesture of goodwill"
        )

        return rentalPeriod.copy(
            invoicedStart = chargedGoodwill.goodwillEnd,
            chargedGoodwill = chargedGoodwill
        )
    }
}
