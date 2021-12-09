package wtf.meier.tariff.interpreter.model.goodwill

import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.tariff.Tariff

interface IGoodwillCalculator {
    fun calculateGoodwill(tariff: Tariff, rentalPeriod: RentalPeriod): RentalPeriod
}