package wtf.meier.tariff.interpreter.model.tariff

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import wtf.meier.tariff.interpreter.IVisitable
import wtf.meier.tariff.interpreter.IVisitor
import wtf.meier.tariff.interpreter.model.Currency
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.goodwill.Goodwill
import wtf.meier.tariff.interpreter.model.rate.Rate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.serializer.CurrencySerializer
import kotlin.jvm.JvmInline
import kotlin.time.Duration.Companion.days

@JvmInline
@Serializable
value class TariffId(val id: Long)

class InvalidTariffFormatException(message: String) : RuntimeException(message)

@Serializable
sealed class Tariff : IVisitable {
    abstract val id: TariffId
    abstract val rates: Set<Rate>
    abstract val billingInterval: Interval?
    abstract val goodwill: Goodwill?
    abstract val currency: Currency
    abstract override fun accept(visitor: IVisitor)
}

@Serializable
@SerialName("SlotBasedTariff")
data class SlotBasedTariff(
    override val id: TariffId,
    override val rates: Set<Rate>,
    override val billingInterval: Interval?,
    @Serializable(with = CurrencySerializer::class)
    override val currency: Currency,
    override val goodwill: Goodwill? = null,
    val slots: Set<Slot>
) : Tariff() {
    @Serializable
    data class Slot(
        val start: Interval,
        val end: Interval?,
        val rate: RateId
    ) : IVisitable {
        fun matches(start: Instant, end: Instant): Boolean {
            val duration = end.toEpochMilliseconds() - start.toEpochMilliseconds()

            // we want to exclude users that are riding exactly the amount of where the interval starts
            // so we start counting at interval + 1 ms
            val t1 = this.start.inWholeMilliseconds + 1

            return t1 <= duration
        }

        override fun accept(visitor: IVisitor) {
            visitor.visitSlot(this)
        }

        val duration: Interval
            get() = end?.minus(start) ?: Int.MAX_VALUE.days
    }

    override fun accept(visitor: IVisitor) {
        visitor.visitSlotBasedTariff(this)
    }

}

