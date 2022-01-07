package wtf.meier.tariff.interpreter.model.rate

import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.calculator.FixedRateCalculator
import wtf.meier.tariff.interpreter.model.rate.calculator.TimeBaseRateCalculator
import java.time.Instant
import java.util.*

class RateCalculator(
    private val timeBaseRateCalculator: TimeBaseRateCalculator = TimeBaseRateCalculator(),
    private val fixedRateCalculator: FixedRateCalculator = FixedRateCalculator()
) {
    data class CalculatedPrice(
        val price: Price,
        val currency: Currency,
        val description: String,
        val calculationStart: Instant,
        val calculationEnd: Instant
    )

    fun calculate(rate: Rate, rentalStart: Instant, rentalEnd: Instant): CalculatedPrice =
        when (rate) {
            is TimeBasedRate -> timeBaseRateCalculator.calculate(rate, rentalStart, rentalEnd)
            is FixedRate -> fixedRateCalculator.calculate(rate, rentalStart, rentalEnd)
        }
}