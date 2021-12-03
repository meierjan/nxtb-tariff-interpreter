package wtf.meier.tariff.interpreter

import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.tariff.Tariff

interface IFreeMinutesCalculator {
    fun calculateFreeMinutes(tariff: Tariff, rentalPeriod: RentalPeriod): RentalPeriod
}