package wtf.meier.tariff.interpreter.model.rate

import kotlinx.datetime.Instant
import wtf.meier.tariff.interpreter.model.Currency
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.calculator.FixedRateCalculator
import wtf.meier.tariff.interpreter.model.rate.calculator.TimeBaseRateCalculator

class RateCalculator(
    private val fixedRateCalculator: FixedRateCalculator = FixedRateCalculator(),
    private val timeBaseRateCalculator: TimeBaseRateCalculator = TimeBaseRateCalculator(),
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
            is FixedRate -> fixedRateCalculator.calculate(rate, rentalStart, rentalEnd)
            is TimeBasedRate -> timeBaseRateCalculator.calculate(rate, rentalStart, rentalEnd)
        }
}