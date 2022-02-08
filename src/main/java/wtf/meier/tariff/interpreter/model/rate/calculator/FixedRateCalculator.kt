package wtf.meier.tariff.interpreter.model.rate.calculator

import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.extension.minOf
import wtf.meier.tariff.interpreter.extension.maxOf
import wtf.meier.tariff.interpreter.model.Price

class FixedRateCalculator {
    fun calculate(rate: FixedRate, ratePeriod: RateCalculator.RatePeriod): RateCalculator.CalculatedPrice =
        RateCalculator.CalculatedPrice(
            price = maxOf(minOf(rate.price, ratePeriod.remainingPrice), Price(0)),
            currency = rate.currency,
            description = "Fixed Price",
            calculationStart = ratePeriod.rentalStart,
            calculationEnd = ratePeriod.rentalEnd
        )
}