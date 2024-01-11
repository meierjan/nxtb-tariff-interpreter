package wtf.meier.tariff.interpreter.model.goodwill.calculator

import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.ChargedGoodwill
import wtf.meier.tariff.interpreter.model.goodwill.DynamicGoodwill

object DynamicGoodwillCalculator {
    fun calculateGoodwill(
        dynamicGoodwill: DynamicGoodwill,
        rentalPeriod: RentalPeriod,
    ): RentalPeriod {
        val calculatedGoodwill =
            rentalPeriod.duration * (dynamicGoodwill.deductibleProportionInPercentage / 100)

        val chargedGoodwill = ChargedGoodwill(
            goodwillStart = rentalPeriod.invoicedEnd - calculatedGoodwill,
            goodwillEnd = rentalPeriod.invoicedEnd,
            description = "${calculatedGoodwill.inWholeMinutes}"
        )
        return rentalPeriod.copy(
            invoicedEnd = chargedGoodwill.goodwillStart,
            chargedGoodwill = chargedGoodwill
        )
    }

}
