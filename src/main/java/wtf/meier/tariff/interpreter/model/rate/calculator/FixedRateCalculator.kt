package wtf.meier.tariff.interpreter.model.rate.calculator

import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import java.time.Instant

class FixedRateCalculator {
    fun calculate(rate: FixedRate, rentalStart: Instant, rentalEnd: Instant): RateCalculator.CalculatedPrice =
        RateCalculator.CalculatedPrice(
            price = rate.price,
            currency = rate.currency,
            description = "Fixed Price"
        )

}