package wtf.meier.tariff.interpreter.model.tariff

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import wtf.meier.tariff.interpreter.extension.durationMillis
import wtf.meier.tariff.interpreter.extension.minus
import wtf.meier.tariff.interpreter.helper.serializer.RateIdSerializer
import wtf.meier.tariff.interpreter.helper.serializer.TariffIdSerializer
import wtf.meier.tariff.interpreter.helper.serializer.TimeZoneSerializer
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.rate.Rate
import wtf.meier.tariff.interpreter.model.rate.RateId
import java.time.DayOfWeek
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

// changed from inline to data class, because inline classes are unsupported by kotlin serialization
data class TariffId(val id: Long)

class InvalidTariffFormatException(message: String) : RuntimeException(message)

@Serializable
sealed class Tariff {
    abstract val id: TariffId
    abstract val freeSeconds: Int
    abstract val rates: Set<Rate>
    abstract val billingInterval: Interval?
    abstract val fairTariff: Boolean

}

@Serializable
@SerialName("SlotBasedTariff")
class SlotBasedTariff(
    @Serializable(with = TariffIdSerializer::class)
    override val id: TariffId,
    override val freeSeconds: Int,
    override val rates: Set<Rate>,
    override val billingInterval: Interval?,
    override val fairTariff: Boolean = false,
    val slots: Set<Slot>
) : Tariff() {
    @Serializable
    data class Slot(
        val start: Interval,
        val end: Interval?,
        @Serializable(with = RateIdSerializer::class)
        val rate: RateId
    ) {
        fun matches(start: Instant, end: Instant): Boolean {
            val duration = end.toEpochMilli() - start.toEpochMilli()

            // we want to exclude users that are riding exactly the amount of where the interval starts
            // so we start counting at interval + 1 ms
            val t1 = this.start.durationMillis() + 1

            return t1 <= duration
        }

        val duration: Interval
            get() = end?.minus(start) ?: Interval(Int.MAX_VALUE, TimeUnit.DAYS)
    }

}

@Serializable
@SerialName("TimeBasedTariff")
data class TimeBasedTariff(
    @Serializable(with = TariffIdSerializer::class)
    override val id: TariffId,
    override val freeSeconds: Int,
    override val rates: Set<Rate>,
    override val billingInterval: Interval?,
    override val fairTariff: Boolean = false,
    @Serializable(with = TimeZoneSerializer::class)
    val timeZone: TimeZone,
    val timeSlots: List<TimeSlot>
) : Tariff() {
    @Serializable
    data class TimeSlot(
        val from: Time,
        val to: Time,
        @Serializable(with = RateIdSerializer::class)
        val rate: RateId
    ) {
        @Serializable
        data class Time(
            val day: DayOfWeek,
            val hour: Int,
            val minutes: Int
        ) : Comparable<Time> {
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
        }
    }

}

@Serializable
@SerialName("DayBasedTariff")
data class DayBasedTariff(
    @Serializable(with = TariffIdSerializer::class)
    override val id: TariffId,
    override val freeSeconds: Int,
    override val rates: Set<Rate>,
    override val billingInterval: Interval?,
    override val fairTariff: Boolean = false,
    @Serializable(with = TimeZoneSerializer::class)
    val timeZone: TimeZone
) : Tariff()
