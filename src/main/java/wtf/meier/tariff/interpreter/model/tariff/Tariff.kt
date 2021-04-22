package wtf.meier.tariff.interpreter.model.tariff

import wtf.meier.tariff.interpreter.extension.durationMillis
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.rate.Rate
import wtf.meier.tariff.interpreter.model.rate.RateId
import java.time.DayOfWeek
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit


@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class TariffId(val id: Long)

class InvalidTariffFormatException(message: String) : RuntimeException(message)

sealed class Tariff {
    abstract val id: TariffId
    abstract val freeSeconds: Int
    abstract val rates: Set<Rate>
    abstract val billingInterval: Interval?

}

data class SlotBasedTariff(
    override val id: TariffId,
    override val freeSeconds: Int,
    override val rates: Set<Rate>,
    override val billingInterval: Interval?,
    val slots: Set<Slot>
) : Tariff() {
    data class Slot(
        val start: Interval,
        val end: Interval?,
        val rate: RateId
    ) {
        fun matches(start: Instant, end: Instant): Boolean {
            val duration = end.toEpochMilli() - start.toEpochMilli()

            // we want to exclude users that are riding exactly the amount of where the interval starts
            // so we start counting at interval + 1 ms
            val t1 = this.start.durationMillis() + 1

            return t1 <= duration
        }
    }
}


data class TimeBasedTariff(
    override val id: TariffId,
    override val freeSeconds: Int,
    override val rates: Set<Rate>,
    override val billingInterval: Interval?,
    val timeZone: TimeZone,
    val timeSlots: List<TimeSlot>
) : Tariff() {

    data class TimeSlot(
        val from: Time,
        val to: Time,
        val rate: RateId
    ) {
        data class Time(
            val day: DayOfWeek,
            val hour: Int,
            val minutes: Int
        ) : Comparable<Time> {
            override fun compareTo(other: Time): Int =
                if (day == other.day) {
                    if(hour == other.hour) {
                        if(minutes == other.minutes) {
                            0
                        } else {
                            minutes.compareTo(other.minutes)
                        }
                    }  else {
                        hour.compareTo(other.hour)
                    }
                } else {
                    day.compareTo(other.day)
                }
            }
    }

}

data class DayBasedTariff(
    override val id: TariffId,
    override val freeSeconds: Int,
    override val rates: Set<Rate>,
    override val billingInterval: Interval?,
    val timeZone: TimeZone
) : Tariff()
