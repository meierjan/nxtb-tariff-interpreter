package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.extension.toReceipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.InvalidTariffFormatException
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.util.CyclicListIterator

class SlotBasedTariffCalculator(
    private val rateCalculator: RateCalculator = RateCalculator()
) {

    fun calculate(tariff: SlotBasedTariff, rentalPeriod: RentalPeriod): Receipt {
        val positions = mutableListOf<RateCalculator.CalculatedPrice>()
        val rateMap = tariff.rates.associateBy { it.id }

        var slotStart = rentalPeriod.invoicedStart

        if (rentalPeriod.invoicedStart >= rentalPeriod.invoicedEnd) return positions.toReceipt(
            currency = tariff.currency,
            chargedGoodwill = rentalPeriod.chargedGoodwill
        )

        var currentBillingEnd = if (tariff.billingInterval == null) {
            rentalPeriod.invoicedEnd
        } else {
            rentalPeriod.invoicedStart.plus(tariff.billingInterval)
        }

        val filteredSlots =
            tariff.slots.filter { it.matches(rentalPeriod.invoicedStart, currentBillingEnd) }
        val sortedSlots = filteredSlots.sortedBy { it.end ?: Interval.INFINITE }
        val slotIterator = CyclicListIterator(sortedSlots)


        var currentSlot = slotIterator.next()


        var slotEnd =
            minOf(
                rentalPeriod.invoicedEnd,
                currentBillingEnd,
                slotStart.plus(currentSlot.end ?: Interval.INFINITE)
            )

        while (slotStart < rentalPeriod.invoicedEnd) {

            val rate = rateMap[currentSlot.rate]
                ?: throw InvalidTariffFormatException("Rate with id ${currentSlot.rate.id} referenced but not defined")

            positions.add(rateCalculator.calculate(rate, slotStart, slotEnd))

            slotStart = slotEnd
            if (tariff.billingInterval != null && currentBillingEnd == slotEnd) {
                currentBillingEnd = currentBillingEnd.plus(tariff.billingInterval)
            }
            if (currentBillingEnd != slotEnd) {
                currentSlot = slotIterator.next()
            }
            slotEnd =
                minOf(
                    rentalPeriod.invoicedEnd,
                    currentBillingEnd,
                    slotStart.plus(currentSlot.duration)
                )

        }
        return positions.toReceipt(
            currency = tariff.currency,
            chargedGoodwill = rentalPeriod.chargedGoodwill
        )
    }
}
