package wtf.meier.tariff.interpreter.model.rate.calculator

import wtf.meier.tariff.interpreter.extension.durationMillis
import wtf.meier.tariff.interpreter.model.extension.max
import wtf.meier.tariff.interpreter.model.extension.min
import wtf.meier.tariff.interpreter.model.extension.plus
import wtf.meier.tariff.interpreter.model.extension.times
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import java.time.Instant
import kotlin.math.ceil

class TimeBaseRateCalculator {
    fun calculate(rate: TimeBasedRate, rentalStart: Instant, rentalEnd: Instant): RateCalculator.CalculatedPrice {
        val intervalLength = rate.interval.durationMillis()
        val durationInMillis = rentalEnd.toEpochMilli() - rentalStart.toEpochMilli()

        // round up so we are also counting started intervals as full
        val intervals = ceil(durationInMillis / intervalLength.toFloat()).toInt()
        val timePrice = rate.pricePerInterval * intervals + rate.basePrice

        val price = min(rate.maxPrice, max(rate.minPrice, timePrice))

        return RateCalculator.CalculatedPrice(
            price = price,
            currency = rate.currency,
            description = "Day Price"
        )
    }
}