package wtf.meier.tariff.interpreter.model

import java.util.*
import java.util.concurrent.TimeUnit

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class RateId(val id: Long)

sealed class Rate {
    abstract val id: RateId
    abstract val currency: Currency
}

data class Price(
    val credit: Long,
)

data class Interval(
    val timeAmount: Int,
    val timeUnit: TimeUnit
)


data class TimeBasedRate(
    override val id: RateId,
    override val currency: Currency,
    val interval: Interval,
    val basePrice: Price,
    val pricePerInterval: Price,
    val maxPrice: Price,
    val minPrice: Price
) : Rate()

data class FixedRate(
    override val id: RateId,
    override val currency: Currency,
    val price: Price
) : Rate()