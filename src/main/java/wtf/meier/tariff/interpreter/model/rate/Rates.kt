package wtf.meier.tariff.interpreter.model.rate

import kotlinx.serialization.*
import wtf.meier.tariff.interpreter.IVisitable
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.IVisitor
import wtf.meier.tariff.serializer.CurrencySerializer
import java.util.*

// changed from inline to data class, because inline classes are unsupported by kotlin serialization
@JvmInline
@Serializable
value class RateId(val id: Long)

@Serializable
sealed class Rate : IVisitable {
    abstract val id: RateId
    abstract val currency: Currency

    abstract override fun accept(visitor: IVisitor)
}

@Serializable
@SerialName("TimeBasedRate")
data class TimeBasedRate(
    override val id: RateId,
    @Serializable(with = CurrencySerializer::class)
    override val currency: Currency,
    val interval: Interval,
    val basePrice: Price,
    val pricePerInterval: Price,
    val maxPrice: Price,
    val minPrice: Price
) : Rate() {
    override fun accept(visitor: IVisitor) {
        visitor.visitTimeBasedRate(this)
    }
}

@Serializable
@SerialName("FixedRate")
data class FixedRate(
    override val id: RateId,
    @Serializable(with = CurrencySerializer::class)
    override val currency: Currency,
    val price: Price
) : Rate() {
    override fun accept(visitor: IVisitor) {
        visitor.visitFixedRate(this)
    }
}