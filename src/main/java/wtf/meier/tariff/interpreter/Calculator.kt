package wtf.meier.tariff.interpreter

import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffCalculator


class Calculator(
    private val calculator: TariffCalculator = TariffCalculator(),
    private val freeMinutesCalculator: IFreeMinutesCalculator = FreeMinutesCalculator
) : ICalculator {


    override fun calculate(tariff: Tariff, rentalPeriod: RentalPeriod): Receipt {
        return calculator.calculate(tariff, freeMinutesCalculator.calculateFreeMinutes(tariff, rentalPeriod))
    }
}