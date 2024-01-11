package wtf.meier.tariff.interpreter.model.rate.calculator

import kotlinx.datetime.Instant
import wtf.meier.tariff.interpreter.model.extension.max
import wtf.meier.tariff.interpreter.model.extension.min
import wtf.meier.tariff.interpreter.model.extension.plus
import wtf.meier.tariff.interpreter.model.extension.times
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import kotlin.math.ceil

class TimeBaseRateCalculator {
    fun calculate(
        rate: TimeBasedRate,
        rentalStart: Instant,
        rentalEnd: Instant
    ): RateCalculator.CalculatedPrice {
        val intervalLength = rate.interval.inWholeMilliseconds
        val durationInMillis = rentalEnd.toEpochMilliseconds() - rentalStart.toEpochMilliseconds()

        // round up so we are also counting started intervals as full
        val intervals = ceil(durationInMillis / intervalLength.toFloat()).toInt()
        val timePrice = rate.pricePerInterval * intervals + rate.basePrice

        val price = min(rate.maxPrice, max(rate.minPrice, timePrice))

        val description = if (price == rate.maxPrice) {
            "MaxPrice in interval"
        } else if (price == rate.minPrice) {
            "MinPrice in interval"
        } else {
            "${rate.pricePerInterval.credit / 100}${rate.currency}  *  $intervals + ${rate.basePrice.credit / 100}${rate.currency}"
        }

        return RateCalculator.CalculatedPrice(
            price = price,
            currency = rate.currency,
            description = description,
            calculationStart = rentalStart,
            calculationEnd = rentalEnd
        )
    }
}