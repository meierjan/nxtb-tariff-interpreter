package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.extension.forwardBefore
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.extension.toReceipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff
import wtf.meier.tariff.interpreter.util.CyclicList
import java.time.Duration
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

class TimeBasedTariffCalculator(
    private val rateCalculator: RateCalculator = RateCalculator()
) {
    fun calculate(tariff: TimeBasedTariff, rentalPeriod: RentalPeriod): Receipt {


        val ratesById = tariff.rates.associateBy { it.id }
        val zonedRentalStart = rentalPeriod.invoicedStart.atZone(tariff.timeZone.toZoneId())
        val zonedRentalEnd = rentalPeriod.invoicedEnd.atZone(tariff.timeZone.toZoneId())

        // sort slots by start-time
        val sortedSlots = tariff.timeSlots.sortedBy { it.from }

        val firstIntersectingSlot: TimeBasedTariff.TimeSlot = firstIntersectingSlot(sortedSlots, zonedRentalStart)
            ?: throw RuntimeException("not slot found -> slots need to be exhaustive")


        val slotIterator = CyclicList(sortedSlots).listIterator()
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

        return bill.toReceipt(
            currency = tariff.currency,
            chargedGoodwill = rentalPeriod.chargedGoodwill
        )
    }


    fun firstIntersectingSlot(list: List<TimeBasedTariff.TimeSlot>, at: ZonedDateTime): TimeBasedTariff.TimeSlot? {
        // Check all slots and choose the one with the closest starting date
        return list.minByOrNull { slot ->
            val (day, hour, minute) = slot.from

            // Put the slot start into the same week and timezone of the requested date
            var adjuster = TemporalAdjusters.previousOrSame(day)
            var slotStart = at.with(adjuster)
                .withHour(hour)
                .withMinute(minute)

            // Adjust the slot if it happen to be later that same day
            if (slotStart > at) {
                adjuster = TemporalAdjusters.previous(day)
                slotStart = at.with(adjuster)
                    .withHour(hour)
                    .withMinute(minute)
            }

            Duration.between(slotStart, at)
        }
    }


}