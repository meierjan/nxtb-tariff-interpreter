package wtf.meier.tariff.interpreter

import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.tariff.Tariff

interface ICalculator {
    fun calculate(tariff: Tariff, rentalPeriod: RentalPeriod): Receipt
}

