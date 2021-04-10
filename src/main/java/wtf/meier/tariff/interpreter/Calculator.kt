package wtf.meier.tariff.interpreter

import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffCalculator
import java.util.*


class Calculator(
    private val calculator: TariffCalculator = TariffCalculator()
) : ICalculator {


    override fun calculate(tariff: Tariff, start: Date, end: Date): Receipt =
        calculator.calculate(tariff, start, end)

}