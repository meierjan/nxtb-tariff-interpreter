package wtf.meier.tariff.interpreter.model.rate

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import wtf.meier.tariff.interpreter.helper.serializer.CurrencySerializer
import wtf.meier.tariff.interpreter.helper.serializer.RateIdSerializer
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import java.util.*

// changed from inline to data class, because inline classes are unsupported by kotlin serialization
data class RateId(val id: Long)

@Serializable
sealed class Rate {
    abstract val id: RateId
    abstract val currency: Currency

}

@Serializable
@SerialName("TimeBasedRate")
data class TimeBasedRate(
    @Serializable(with = RateIdSerializer::class)
    override val id: RateId,
    @Serializable(with = CurrencySerializer::class)
    override val currency: Currency,
    val interval: Interval,
    val basePrice: Price,
    val pricePerInterval: Price,
    val maxPrice: Price,
    val minPrice: Price
) : Rate()

@Serializable
@SerialName("FixedRate")
data class FixedRate(
    @Serializable(with = RateIdSerializer::class)
    override val id: RateId,
    @Serializable(with = CurrencySerializer::class)
    override val currency: Currency,
    val price: Price
) : Rate()