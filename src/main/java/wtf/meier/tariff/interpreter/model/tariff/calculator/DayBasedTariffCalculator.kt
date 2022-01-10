package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.exception.InvalidSlotException
import wtf.meier.tariff.interpreter.extension.compareTo
import wtf.meier.tariff.interpreter.extension.min
import wtf.meier.tariff.interpreter.extension.toInterval
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.extension.toReceipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.DayBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import java.time.Instant
import java.time.LocalTime.MAX
import java.time.LocalTime.MIN
import java.time.Period
import java.util.concurrent.TimeUnit


class DayBasedTariffCalculator(
    private val rateCalculator: RateCalculator = RateCalculator()
) {

    fun calculate(tariff: DayBasedTariff, rentalPeriod: RentalPeriod): Receipt {

        val timeZoneId = tariff.timeZone.toZoneId()
        val startLocalInstant = rentalPeriod.invoicedStart.atZone(timeZoneId)
        val endLocalInstant = rentalPeriod.invoicedEnd.atZone(timeZoneId)
        val daysRented = Period.between(startLocalInstant.toLocalDate(), endLocalInstant.toLocalDate()).days + 1


        val slot: DayBasedTariff.Slot? = findMatchingSlot(tariff.slots, rentalPeriod.duration, daysRented)

        when (slot) {
            is DayBasedTariff.RentalSynchronizedSlot -> {
                val filteredSlots = tariff.slots.filterIsInstance<DayBasedTariff.RentalSynchronizedSlot>()
                val slotBasedTariffSlots =
                    filteredSlots.map { SlotBasedTariff.Slot(start = it.start, end = it.end, rate = it.rate) }.toSet()
                val slotBasedTariff = SlotBasedTariff(
                    id = tariff.id,
                    rates = tariff.rates,
                    billingInterval = tariff.billingInterval,
                    currency = tariff.currency,
                    slots = slotBasedTariffSlots,
                )
                return SlotBasedTariffCalculator().calculate(slotBasedTariff, rentalPeriod)
            }
            is DayBasedTariff.DaySynchronisedSlot -> {
                val rateMap = tariff.rates.associateBy { it.id }
                val positions = mutableListOf<RateCalculator.CalculatedPrice>()

                var currentDay = startLocalInstant
                while (!endLocalInstant.isBefore(currentDay)) {

                    val relativeStart = currentDay.with(MIN)
                    val relativeEnd = min(currentDay.with(MAX), endLocalInstant)

                    val startInstant = relativeStart.toInstant()
                    val endInstant = relativeEnd.toInstant()

                    val rate = rateMap[slot.rate]
                    val position = rateCalculator.calculate(rate!!, startInstant, endInstant)
                    positions.add(position)

                    currentDay = currentDay.plusDays(1)
                }
                return positions.toReceipt(
                    chargedGoodwill = rentalPeriod.chargedGoodwill,
                    currency = tariff.currency
                )
            }

            else -> throw InvalidSlotException("can't find valid slot in tariff: $tariff")

        }
    }

    fun findMatchingSlot(slots: Set<DayBasedTariff.Slot>, duration: Instant, daysRented: Int): DayBasedTariff.Slot? {
        var slot: DayBasedTariff.Slot? = null

        slots.forEach {

            when (it) {
                is DayBasedTariff.RentalSynchronizedSlot -> if (it.start < duration.toInterval() && (it.end ?: Interval(
                        Int.MAX_VALUE,
                        TimeUnit.DAYS
                    )) >= duration.toInterval()
                ) return it
                is DayBasedTariff.DaySynchronisedSlot -> if (it.startDay <= daysRented && (it.endDay
                        ?: Int.MAX_VALUE) > daysRented
                )
                    slot = it
            }
        }
        return slot
    }
}



