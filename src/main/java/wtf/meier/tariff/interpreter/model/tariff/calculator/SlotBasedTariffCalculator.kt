package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.extension.durationMillis
import wtf.meier.tariff.interpreter.extension.plus
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.extension.toReceipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.InvalidTariffFormatException
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.util.CyclicList
import java.time.Instant
import java.util.concurrent.TimeUnit

class SlotBasedTariffCalculator(
    private val rateCalculator: RateCalculator = RateCalculator()
) {

    fun calculate(tariff: SlotBasedTariff, rentalStart: Instant, rentalEnd: Instant): Receipt {
        val positions = mutableListOf<RateCalculator.CalculatedPrice>()
        val rateMap = tariff.rates.associateBy { it.id }

        var slotStart = rentalStart
        var currentBillingEnd = if (tariff.billingInterval == null) {
            rentalEnd
        } else {
            rentalStart.plus(tariff.billingInterval)
        }

        val filteredSlots = tariff.slots.filter { it.matches(rentalStart, currentBillingEnd) }
        val sortedSlots = filteredSlots.sortedBy { it.end?.durationMillis() ?: Long.MAX_VALUE }
        val sortedCyclicSlots = CyclicList(sortedSlots)
        var currentSlotIndex = 0

        var slotEnd =
            minOf(
                rentalEnd,
                currentBillingEnd,
                slotStart.plus(sortedCyclicSlots[currentSlotIndex].end ?: Interval(Integer.MAX_VALUE, TimeUnit.DAYS))
            )

        while (slotStart < rentalEnd) {

            val rate = rateMap[sortedCyclicSlots[currentSlotIndex].rate]
                ?: throw InvalidTariffFormatException("Rate with id ${sortedCyclicSlots[currentSlotIndex].rate.id} referenced but not defined")

            positions.add(rateCalculator.calculate(rate, slotStart, slotEnd))

            slotStart = slotEnd
            if (tariff.billingInterval != null && currentBillingEnd == slotEnd) {
                currentBillingEnd = currentBillingEnd.plus(tariff.billingInterval)
            }
            if (currentBillingEnd != slotEnd) {
                currentSlotIndex++
            }
            slotEnd =
                minOf(
                    rentalEnd,
                    currentBillingEnd,
                    slotStart.plus(sortedCyclicSlots[currentSlotIndex].end ?: Interval(Integer.MAX_VALUE, TimeUnit.DAYS)))
        }
        return positions.toReceipt()
    }
}