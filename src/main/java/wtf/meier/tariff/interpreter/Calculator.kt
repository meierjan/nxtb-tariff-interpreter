package wtf.meier.tariff.interpreter

import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.GoodwillCalculator
import wtf.meier.tariff.interpreter.model.goodwill.IGoodwillCalculator
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffCalculator
import wtf.meier.tariff.validator.Validator


class Calculator(
    private val calculator: TariffCalculator = TariffCalculator(),
    private val goodwillCalculator: IGoodwillCalculator = GoodwillCalculator
) : ICalculator {


    override fun calculate(tariff: Tariff, rentalPeriod: RentalPeriod): Receipt {
        rentalPeriod.accept(Validator)
        return calculator.calculate(tariff, goodwillCalculator.calculateGoodwill(tariff, rentalPeriod))
    }
}