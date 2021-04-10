package wtf.meier.tariff.interpreter.model.rate

import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.calculator.FixedRateCalculator
import wtf.meier.tariff.interpreter.model.rate.calculator.TimeBaseRateCalculator
import java.util.*

class RateCalculator(
    private val timeBaseRateCalculator: TimeBaseRateCalculator = TimeBaseRateCalculator(),
    private val fixedRateCalculator: FixedRateCalculator = FixedRateCalculator()
) {

    fun calculate(rate: Rate, rentalStart: Date, rentalEnd: Date): Price =
        when (rate) {
            is TimeBasedRate -> timeBaseRateCalculator.calculate(rate, rentalStart, rentalEnd)
            is FixedRate -> fixedRateCalculator.calculate(rate, rentalStart, rentalEnd)
        }
}