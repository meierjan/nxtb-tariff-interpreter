package wtf.meier.tariff.interpreter.model.rate.calculator

import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import java.util.*

class FixedRateCalculator {
    fun calculate(rate: FixedRate, rentalStart: Date, rentalEnd: Date): Price =
        rate.price
    
}