package wtf.meier.tariff.interpreter.model.goodwill

import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.calculator.DynamicGoodwillCalculator
import wtf.meier.tariff.interpreter.model.goodwill.calculator.FreeMinutesCalculator
import wtf.meier.tariff.interpreter.model.goodwill.calculator.StaticGoodwillcalculator
import wtf.meier.tariff.interpreter.model.tariff.Tariff

object GoodwillCalculator : IGoodwillCalculator {

    override fun calculateGoodwill(tariff: Tariff, rentalPeriod: RentalPeriod): RentalPeriod {
        var calculatedRentalPeriod: RentalPeriod = rentalPeriod.copy()
        val goodwill = tariff.goodwill
        when (goodwill) {
            is StaticGoodwill -> calculatedRentalPeriod =
                StaticGoodwillcalculator.calculateGoodwill(goodwill, calculatedRentalPeriod).copy()
            is FreeMinutes -> calculatedRentalPeriod =
                FreeMinutesCalculator.calculateGoodwill(goodwill, calculatedRentalPeriod).copy()
            is DynamicGoodwill -> calculatedRentalPeriod =
                DynamicGoodwillCalculator.calculateGoodwill(goodwill, calculatedRentalPeriod).copy()
        }
        return calculatedRentalPeriod
    }
}
