package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.extension.forwardBefore
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.extension.toReceipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff
import wtf.meier.tariff.interpreter.util.CyclicListIterator
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

class TimeBasedTariffCalculator(
        private val rateCalculator: RateCalculator = RateCalculator()
) {
    fun calculate(tariff: TimeBasedTariff, rentalStart: Instant, rentalEnd: Instant): Receipt {


        val ratesById = tariff.rates.associateBy { it.id }
        val zonedRentalStart = rentalStart.atZone(tariff.timeZone.toZoneId())
        val zonedRentalEnd = rentalEnd.atZone(tariff.timeZone.toZoneId())

        // sort slots by start-time
        val sortedSlots = tariff.timeSlots.sortedBy { it.from }

        val firstIntersectingSlot: TimeBasedTariff.TimeSlot = firstIntersectingSlot(sortedSlots, zonedRentalStart)
                ?: throw RuntimeException("not slot found -> slots need to be exhaustive")


        val slotIterator = CyclicListIterator(sortedSlots)
                .forwardBefore(firstIntersectingSlot)


        val bill = mutableListOf<RateCalculator.CalculatedPrice>()

        // First interval: start, otherwise slot end from before
        var lastSlotEnd = zonedRentalStart

        for (slot in slotIterator) {
            // slot is firstIntersectingSlot
            val currentRate = ratesById[slot.rate]
                    ?: throw IllegalStateException("referenced rate ${slot.rate} specified but not found")

            val slotEndTime = slot.to

            val adjuster = TemporalAdjusters.nextOrSame(slotEndTime.day)
            val slotEnd = lastSlotEnd.with(adjuster)
                    .withHour(slotEndTime.hour)
                    .withMinute(slotEndTime.minutes)

            // find intersection or rental with slot

            // calculate price
            val intersectionStart = lastSlotEnd.toInstant()
            val intersectionEnd = slotEnd.toInstant()

            val receipt = rateCalculator.calculate(currentRate, intersectionStart, intersectionEnd)
            bill.add(receipt)

            // break if rental-end < slot_end

            lastSlotEnd = slotEnd

            if (zonedRentalEnd < slotEnd) {
                break
            }
        }

        return bill.toReceipt()
    }

    fun firstIntersectingSlot(list: List<TimeBasedTariff.TimeSlot>, at: ZonedDateTime): TimeBasedTariff.TimeSlot? {
        return list.maxByOrNull { slot ->
            val (day, hour, minute) = slot.from

            val adjuster = TemporalAdjusters.previousOrSame(day)
            val slotStart = at.with(adjuster)
                    .withHour(hour)
                    .withMinute(minute)

            var duration = Duration.between(at, slotStart)

            // Disregard slots starting in the future
            if (slotStart > at) {
                // Hack to return a larger duration than possible during a week
                duration = Duration.ofDays(-100)
            }

            // Durations are negative, so in order to get the shortest duration, we need the maximum one
            duration
        }
    }


}