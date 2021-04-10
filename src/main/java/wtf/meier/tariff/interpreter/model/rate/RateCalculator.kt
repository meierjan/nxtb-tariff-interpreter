package wtf.meier.tariff.interpreter.model.rate

import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.calculator.FixedRateCalculator
import wtf.meier.tariff.interpreter.model.rate.calculator.TimeBaseRateCalculator
import java.util.*

class RateCalculator(
    private val timeBaseRateCalculator: TimeBaseRateCalculator = TimeBaseRateCalculator(),
    private val fixedRateCalculator: FixedRateCalculator = FixedRateCalculator()
) {

    fun calculate(rate: Rate, start: Date, end: Date): Price =
        when (rate) {
            is TimeBasedRate -> timeBaseRateCalculator.calculate(rate, start, end)
            is FixedRate -> fixedRateCalculator.calculate(rate, start, end)
        }
}