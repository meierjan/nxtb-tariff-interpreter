package wtf.meier.tariff.interpreter.model.rate.calculator

import kotlinx.datetime.Instant
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateCalculator

class FixedRateCalculator {
    fun calculate(
        rate: FixedRate,
        rentalStart: Instant,
        rentalEnd: Instant
    ): RateCalculator.CalculatedPrice =
        RateCalculator.CalculatedPrice(
            price = rate.price,
            currency = rate.currency,
            description = "Fixed Price",
            calculationStart = rentalStart,
            calculationEnd = rentalEnd
        )

}