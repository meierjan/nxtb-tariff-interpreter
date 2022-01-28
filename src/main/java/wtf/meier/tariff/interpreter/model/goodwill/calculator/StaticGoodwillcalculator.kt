package wtf.meier.tariff.interpreter.model.goodwill.calculator

import wtf.meier.tariff.interpreter.extension.minOf
import wtf.meier.tariff.interpreter.extension.minus
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.ChargedGoodwill
import wtf.meier.tariff.interpreter.model.goodwill.StaticGoodwill

object StaticGoodwillcalculator {
    fun calculateGoodwill(
        goodwill: StaticGoodwill,
        rentalPeriod: RentalPeriod
    ): RentalPeriod {
        val calculatedGoodwill =
            minOf(rentalPeriod.duration, goodwill.duration)

        val chargedGoodwill = ChargedGoodwill(
            goodwillStart = rentalPeriod.invoicedEnd - calculatedGoodwill,
            goodwillEnd = rentalPeriod.invoicedEnd,
            description = "${calculatedGoodwill.timeUnit.toMinutes(calculatedGoodwill.timeAmount.toLong())} minutes were deducted from your travel time as a gesture of goodwill"
        )

        return rentalPeriod.copy(
            invoicedEnd = chargedGoodwill.goodwillStart,
            chargedGoodwill = chargedGoodwill
        )
    }
}