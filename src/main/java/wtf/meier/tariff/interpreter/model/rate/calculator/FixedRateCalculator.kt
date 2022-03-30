package wtf.meier.tariff.interpreter.model.rate.calculator

import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateCalculator

class FixedRateCalculator {
    fun calculate(rate: FixedRate, ratePeriod: RateCalculator.RatePeriod): RateCalculator.CalculatedPrice =
        RateCalculator.CalculatedPrice(
            price = rate.price,
            currency = rate.currency,
            description = "Fixed Price",
            calculationStart = ratePeriod.rentalStart,
            calculationEnd = ratePeriod.rentalEnd
        )
}