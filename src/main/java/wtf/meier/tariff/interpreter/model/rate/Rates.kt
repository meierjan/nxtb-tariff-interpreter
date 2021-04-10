package wtf.meier.tariff.interpreter.model.rate

import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import java.util.*

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class RateId(val id: Long)

sealed class Rate {
    abstract val id: RateId
    abstract val currency: Currency

}

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