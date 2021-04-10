package wtf.meier.tariff.interpreter.model

import wtf.meier.tariff.interpreter.Receipt
import java.time.DayOfWeek
import java.util.*

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class TariffId(val id: Long)

class InvalidTariffFormatException(message: String) : RuntimeException(message)

sealed class Tariff {
    abstract val id: TariffId
    abstract val freeSeconds: Int
    abstract val rates: Set<Rate>
    abstract val billingInterval: Interval?

    abstract fun calculate(rentalStart: Date, rentalEnd: Date): Receipt

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
        fun matches(start: Date, end: Date): Boolean {
            val duration = end.time - start.time

            val t1 = this.start.toMillis()

            return t1 <= duration
        }
    }

    override fun calculate(rentalStart: Date, rentalEnd: Date): Receipt {
        // order edf
        val sortedSlots = slots.sortedBy { it.end?.toMillis() ?: Long.MAX_VALUE }
        val rateMap = rates.associateBy { it.id }

        val finalPrice = mutableListOf<Price>()

        for (slot in sortedSlots) {
            if (slot.matches(rentalStart, rentalEnd)) {

                val rate = rateMap[slot.rate]
                    ?: throw InvalidTariffFormatException("Rate with id ${slot.rate.id} referenced but not defined")

                val slotStart = rentalStart + slot.start
                val slotEnd = if (slot.end == null) {
                    rentalEnd
                } else {
                    min(slotStart + slot.end, rentalEnd)
                }

                finalPrice.add(rate.calculate(slotStart, slotEnd))
            }
        }

        return Receipt(
            finalPrice.sumOf { it.credit }.toInt(),
            currency = Currency.getInstance("EUR")
        )

    }
}

operator fun Date.plus(interval: Interval): Date =
    Date(time + interval.toMillis())

fun min(d1: Date, d2: Date) =
    if (d1.before(d2)) d1 else d2


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

    override fun calculate(rentalStart: Date, rentalEnd: Date): Receipt {
        TODO("Not yet implemented")
    }
}