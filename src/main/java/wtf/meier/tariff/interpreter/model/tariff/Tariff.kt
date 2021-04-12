package wtf.meier.tariff.interpreter.model.tariff

import wtf.meier.tariff.interpreter.extension.durationMillis
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.rate.Rate
import wtf.meier.tariff.interpreter.model.rate.RateId
import java.time.DayOfWeek
import java.time.Instant
import java.util.*


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
            val duration = end.epochSecond - start.epochSecond

            val t1 = this.start.durationMillis()

            return t1 <= duration
        }
    }
}


data class TimeBasedTariff(
    override val id: TariffId,
    override val freeSeconds: Int,
    override val rates: Set<Rate>,
    override val billingInterval: Interval?,
    val timeSlots: List<TimeSlot>
) : Tariff() {
    data class TimeSlot(
        val days: Set<DayOfWeek>,
        val time: Set<TimeSpan>
    )

    data class TimeSpan(
        val from: Time,
        val to: Time
    ) {
        data class Time(
            val hour: Short,
            val minutes: Short
        )
    }

}

data class DayBasedTariff(
    override val id: TariffId,
    override val freeSeconds: Int,
    override val rates: Set<Rate>,
    override val billingInterval: Interval?,
    val timeZone: TimeZone
) : Tariff()
