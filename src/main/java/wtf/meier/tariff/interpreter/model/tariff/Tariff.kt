package wtf.meier.tariff.interpreter.model.tariff

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import wtf.meier.tariff.interpreter.IVisitable
import wtf.meier.tariff.interpreter.extension.durationMillis
import wtf.meier.tariff.interpreter.extension.minus
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.goodwill.Goodwill
import wtf.meier.tariff.interpreter.model.rate.Rate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.IVisitor
import wtf.meier.tariff.serializer.CurrencySerializer
import wtf.meier.tariff.serializer.TimeZoneSerializer
import java.time.DayOfWeek
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

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
            val duration = end.toEpochMilli() - start.toEpochMilli()

            // we want to exclude users that are riding exactly the amount of where the interval starts
            // so we start counting at interval + 1 ms
            val t1 = this.start.durationMillis() + 1

            return t1 <= duration
        }

        override fun accept(visitor: IVisitor) {
            visitor.visitSlot(this)
        }
        
        val duration: Interval
            get() = end?.minus(start) ?: Interval(Int.MAX_VALUE, TimeUnit.DAYS)
    }

    override fun accept(visitor: IVisitor) {
        visitor.visitSlotBasedTariff(this)
    }

}

@Serializable
@SerialName("TimeBasedTariff")
data class TimeBasedTariff(
    override val id: TariffId,
    override val rates: Set<Rate>,
    override val billingInterval: Interval?,
    override val goodwill: Goodwill? = null,
    @Serializable(with = CurrencySerializer::class)
    override val currency: Currency,
    @Serializable(with = TimeZoneSerializer::class)
    val timeZone: TimeZone,
    val timeSlots: List<TimeSlot>
) : Tariff() {
    @Serializable
    data class TimeSlot(
        val from: Time,
        val to: Time,
        val rate: RateId
    ) : IVisitable {
        @Serializable
        data class Time(
            val day: DayOfWeek,
            val hour: Int,
            val minutes: Int
        ) : IVisitable, Comparable<Time> {
            override fun compareTo(other: Time): Int =
                if (day == other.day) {
                    if (hour == other.hour) {
                        if (minutes == other.minutes) {
                            0
                        } else {
                            minutes.compareTo(other.minutes)
                        }
                    } else {
                        hour.compareTo(other.hour)
                    }
                } else {
                    day.compareTo(other.day)
                }

            override fun accept(visitor: IVisitor) {
                visitor.visitTime(this)
            }
        }

        override fun accept(visitor: IVisitor) {
            visitor.visitTimeSlot(this)
        }
    }

    override fun accept(visitor: IVisitor) {
        visitor.visitTimeBasedTariff(this)
    }

}
