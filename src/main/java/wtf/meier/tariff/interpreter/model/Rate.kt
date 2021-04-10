package wtf.meier.tariff.interpreter.model

import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class RateId(val id: Long)

sealed class Rate {

    abstract val id: RateId
    abstract val currency: Currency

    abstract fun calculate(start: Date, end: Date): Price
}

data class Price(
    val credit: Long,
) {
    operator fun times(times: Long): Price =
        Price(times * credit)

    operator fun plus(p2: Price): Price =
        Price(credit + p2.credit)
}

data class Interval(
    val timeAmount: Int,
    val timeUnit: TimeUnit
) {
    fun toMillis(): Long = timeUnit.toMillis(timeAmount.toLong())
}


data class TimeBasedRate(
    override val id: RateId,
    override val currency: Currency,
    val interval: Interval,
    val basePrice: Price,
    val pricePerInterval: Price,
    val maxPrice: Price,
    val minPrice: Price
) : Rate() {

    override fun calculate(start: Date, end: Date): Price {
        val intervalLength = interval.toMillis()
        val durationInMillis = end.time - start.time

        // round up so we are also counting started intervals as full
        val intervals = ceil(durationInMillis / intervalLength.toFloat()).toLong()
        val timePrice = pricePerInterval * intervals + basePrice

        return min(maxPrice, max(minPrice, timePrice))
    }
}

fun max(p1: Price, p2: Price): Price {
    return if (p1.credit < p2.credit) {
        p2
    } else {
        p1
    }
}

fun min(p1: Price, p2: Price): Price {
    return if (p1.credit < p2.credit) {
        p1
    } else {
        p2
    }
}

data class FixedRate(
    override val id: RateId,
    override val currency: Currency,
    val price: Price
) : Rate() {
    override fun calculate(start: Date, end: Date): Price =
        price

}