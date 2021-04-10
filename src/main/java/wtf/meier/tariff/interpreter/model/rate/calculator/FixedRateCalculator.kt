package wtf.meier.tariff.interpreter.model.rate.calculator

import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import java.util.*

class FixedRateCalculator {
    fun calculate(rate: FixedRate, start: Date, end: Date): Price {
        return rate.price
    }
}