package wtf.meier.tariff.interpreter.model

import wtf.meier.tariff.interpreter.Receipt
import java.time.DayOfWeek
import java.util.*

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class TariffId(val id: Long)

sealed class Tariff {
    abstract val id: TariffId
    abstract val freeSeconds: Int
    abstract val rates: Set<Rate>
    abstract val billingInterval: Interval?

    abstract fun calculate(start: Date, end: Date) : Receipt

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
    )

    override fun calculate(start: Date, end: Date): Receipt {
        TODO("Not yet implemented")
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

    override fun calculate(start: Date, end: Date): Receipt {
        TODO("Not yet implemented")
    }
}