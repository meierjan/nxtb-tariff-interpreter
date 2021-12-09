package wtf.meier.tariff.interpreter.model.goodwill

import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.calculator.DynamicGoodwillCalculator
import wtf.meier.tariff.interpreter.model.goodwill.calculator.FreeMinutesCalculator
import wtf.meier.tariff.interpreter.model.goodwill.calculator.StaticGoodwillcalculator
import wtf.meier.tariff.interpreter.model.tariff.Tariff

object GoodwillCalculator : IGoodwillCalculator {

    override fun calculateGoodwill(tariff: Tariff, rentalPeriod: RentalPeriod): RentalPeriod {
        var calculatedRentalPeriod: RentalPeriod = rentalPeriod.copy()
        tariff.goodwill?.forEach {
            when (it) {
                is StaticGoodwill -> calculatedRentalPeriod =
                    StaticGoodwillcalculator.calculateGoodwill(it, calculatedRentalPeriod, tariff.currency).copy()
                is FreeMinutes -> calculatedRentalPeriod =
                    FreeMinutesCalculator.calculateGoodwill(it, calculatedRentalPeriod, tariff.currency).copy()
            }
        }
        //todo find better way to calculate dynamicGoodwill least
        tariff.goodwill?.forEach {
            if (it is DynamicGoodwill) calculatedRentalPeriod =
                DynamicGoodwillCalculator.calculateGoodwill(it, calculatedRentalPeriod, tariff.currency).copy()


        }
        return calculatedRentalPeriod
    }
}
