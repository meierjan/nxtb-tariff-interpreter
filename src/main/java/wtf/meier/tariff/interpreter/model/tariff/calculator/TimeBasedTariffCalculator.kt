package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.extension.*
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.extension.toReceipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff
import wtf.meier.tariff.interpreter.util.CyclicListIterator
import java.time.Duration
import wtf.meier.tariff.interpreter.model.billingInterval.BillingIntervalCalculator
import wtf.meier.tariff.interpreter.model.extension.minus
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.concurrent.TimeUnit

class TimeBasedTariffCalculator(
    private val rateCalculator: RateCalculator = RateCalculator(),
    private val billingIntervalCalculator: BillingIntervalCalculator = BillingIntervalCalculator,

    ) {
    fun calculate(tariff: TimeBasedTariff, rentalPeriod: RentalPeriod): Receipt {
        val bill = mutableListOf<RateCalculator.CalculatedPrice>()
        if (rentalPeriod.duration <= Interval(0, TimeUnit.MINUTES))
            return bill.toReceipt(
                chargedGoodwill = rentalPeriod.chargedGoodwill,
                currency = tariff.currency
            )

        val rentalPeriodToCalculate = billingIntervalCalculator.calculateRemainingTime(tariff, rentalPeriod)


        val ratesById = tariff.rates.associateBy { it.id }
        val zonedRentalStart = rentalPeriodToCalculate.invoicedStart.atZone(tariff.timeZone.toZoneId())
        val zonedRentalEnd = rentalPeriodToCalculate.invoicedEnd.atZone(tariff.timeZone.toZoneId())
        var remainingPrice = tariff.billingInterval.maxPrice


        // sort slots by start-time
        val sortedSlots = tariff.timeSlots.sortedBy { it.from }

        val firstIntersectingSlot = firstIntersectingSlot(sortedSlots, zonedRentalStart)
            ?: throw RuntimeException("not slot found -> slots need to be exhaustive")


        val slotIterator = CyclicListIterator(sortedSlots)
            .forwardBefore(firstIntersectingSlot)

        // First interval: start, otherwise slot end from before
        var lastSlotEnd = zonedRentalStart

        for (slot in slotIterator) {
            if (rentalPeriodToCalculate.duration.durationMillis() <= 0)
                break

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
            val intersectionEnd = minOf(slotEnd.toInstant(), rentalPeriodToCalculate.invoicedEnd)

            val receipt = rateCalculator.calculate(
                currentRate,
                RateCalculator.RatePeriod(
                    rentalStart = intersectionStart,
                    rentalEnd = intersectionEnd
                )
            )

            remainingPrice -= receipt.price

            bill.add(receipt)

            // break if rental-end < slot_end

            lastSlotEnd = slotEnd

            if (zonedRentalEnd < slotEnd || remainingPrice.credit <= 0) {
                break
            }

        }

        // if remainingPrice equals 0, the max value of last started billingInterval is reached.
        if (remainingPrice.credit <= 0)
            return mutableListOf(
                billingIntervalCalculator.calculateTotalRentalWithBillingIntervalMaxPrice(
                    tariff,
                    rentalPeriod
                )
            ).toReceipt(currency = tariff.currency, chargedGoodwill = rentalPeriod.chargedGoodwill)

        billingIntervalCalculator.calculateBillingIntervalPrice(tariff, rentalPeriod)
            ?.let {
                bill.add(it)
            }

        return bill.toReceipt(currency = tariff.currency, chargedGoodwill = rentalPeriod.chargedGoodwill)

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