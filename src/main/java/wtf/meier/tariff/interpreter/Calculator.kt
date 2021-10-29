package wtf.meier.tariff.interpreter

import wtf.meier.tariff.interpreter.extension.RentalPeriod
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.tariff.FreeMinutesCalculator
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffCalculator


class Calculator(
    private val calculator: TariffCalculator = TariffCalculator()
) : ICalculator {


    override fun calculate(tariff: Tariff, rentalPeriod: RentalPeriod): Receipt =

        calculator.calculate(
            tariff, FreeMinutesCalculator.calculateFreeMinutes(tariff, rentalPeriod)
        )

}