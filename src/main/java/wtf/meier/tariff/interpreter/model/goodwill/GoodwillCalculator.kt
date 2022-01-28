package wtf.meier.tariff.interpreter.model.goodwill

import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.calculator.DynamicGoodwillCalculator
import wtf.meier.tariff.interpreter.model.goodwill.calculator.FreeMinutesCalculator
import wtf.meier.tariff.interpreter.model.goodwill.calculator.StaticGoodwillcalculator
import wtf.meier.tariff.interpreter.model.tariff.Tariff

object GoodwillCalculator : IGoodwillCalculator {

    override fun calculateGoodwill(tariff: Tariff, rentalPeriod: RentalPeriod): RentalPeriod =
        when (val goodwill = tariff.goodwill) {
            is StaticGoodwill -> StaticGoodwillcalculator.calculateGoodwill(goodwill, rentalPeriod)
            is FreeMinutes -> FreeMinutesCalculator.calculateGoodwill(goodwill, rentalPeriod)
            is DynamicGoodwill -> DynamicGoodwillCalculator.calculateGoodwill(goodwill, rentalPeriod)
            null -> rentalPeriod
        }

}
