package wtf.meier.tariff.interpreter.model

import java.time.DayOfWeek

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class TariffId(val id: Long)

sealed class Tariff {
    abstract val id: TariffId
    abstract val freeSeconds: Int
    abstract val rates: Set<Rate>
}

data class SlotBasedTariff(
    override val id: TariffId,
    override val freeSeconds: Int,
    override val rates: Set<Rate>,
    val slots: List<Slot>
) : Tariff() {
    data class Slot(
        val start: Interval,
        val end: Interval,
        val rate: RateId
    )
}

data class TimeBasedTariff(
    override val id: TariffId,
    override val freeSeconds: Int,
    override val rates: Set<Rate>,
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