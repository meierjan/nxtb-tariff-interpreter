package wtf.meier.tariff.interpreter.model.rate.calculator

import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import java.time.Instant

class FixedRateCalculator {
    fun calculate(rate: FixedRate, rentalStart: Instant, rentalEnd: Instant): Price =
        rate.price

}