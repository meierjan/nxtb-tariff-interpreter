package wtf.meier.tariff.interpreter.model.rate.calculator

import wtf.meier.tariff.interpreter.extension.toMillis
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.extension.max
import wtf.meier.tariff.interpreter.model.extension.min
import wtf.meier.tariff.interpreter.model.extension.plus
import wtf.meier.tariff.interpreter.model.extension.times
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import java.util.*
import kotlin.math.ceil

class TimeBaseRateCalculator {
    fun calculate(rate: TimeBasedRate, start: Date, end: Date): Price {
        val intervalLength = rate.interval.toMillis()
        val durationInMillis = end.time - start.time

        // round up so we are also counting started intervals as full
        val intervals = ceil(durationInMillis / intervalLength.toFloat()).toLong()
        val timePrice = rate.pricePerInterval * intervals + rate.basePrice

        return min(rate.maxPrice, max(rate.minPrice, timePrice))
    }
}