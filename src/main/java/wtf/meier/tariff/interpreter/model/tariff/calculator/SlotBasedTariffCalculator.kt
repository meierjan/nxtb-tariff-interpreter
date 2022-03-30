package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.extension.durationMillis
import wtf.meier.tariff.interpreter.extension.plus
import wtf.meier.tariff.interpreter.extension.toInstant
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.billingInterval.BillingIntervalCalculator
import wtf.meier.tariff.interpreter.model.extension.minus
import wtf.meier.tariff.interpreter.model.extension.toReceipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.InvalidTariffFormatException
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.util.CyclicListIterator

class SlotBasedTariffCalculator(
    private val rateCalculator: RateCalculator = RateCalculator(),
    private val billingIntervalCalculator: BillingIntervalCalculator = BillingIntervalCalculator
) {
    fun calculate(tariff: SlotBasedTariff, rentalPeriod: RentalPeriod): Receipt {
        val bill = mutableListOf<RateCalculator.CalculatedPrice>()

        // if calculation-end before calculation-start return immediately
        if (rentalPeriod.invoicedStart >= rentalPeriod.invoicedEnd)
            return bill.toReceipt(
                currency = tariff.currency,
                chargedGoodwill = rentalPeriod.chargedGoodwill
            )

        val rentalPeriodToCalculate =
            billingIntervalCalculator.calculateRemainingTime(rentalPeriod = rentalPeriod, tariff = tariff)

        // max price of the last started interval
        var remainingPrice = tariff.billingInterval.maxPrice
        val rateMap = tariff.rates.associateBy { it.id }
        var slotStart = rentalPeriodToCalculate.invoicedStart

        // remove all slots that do not intersect the rental
        val filteredSlots = tariff.slots.filter {
            it.matches(
                rentalPeriodToCalculate.invoicedStart,
                rentalPeriodToCalculate.invoicedEnd
            )
        }

        //sort slots by start-time
        val sortedSlots = filteredSlots.sortedBy { it.end.durationMillis() }
        val slotIterator = CyclicListIterator(sortedSlots)
        var currentSlot = slotIterator.next()

        // slot end is min of rental end or slot end
        var slotEnd =
            minOf(
                rentalPeriodToCalculate.invoicedEnd,
                slotStart.plus(currentSlot.end)
            )

        // calculates price of each slot
        while (slotStart < rentalPeriodToCalculate.invoicedEnd) {
            val rate = rateMap[currentSlot.rate]
                ?: throw InvalidTariffFormatException("Rate with id ${currentSlot.rate.id} referenced but not defined")

            // calculates price of current slot
            val position = rateCalculator.calculate(
                rate,
                RateCalculator.RatePeriod(rentalStart = slotStart, rentalEnd = slotEnd)
            )
            bill.add(position)

            // subtraction of the current slot price from remaining price
            remainingPrice -= position.price
            // if remaining price lesser than zero, the maxBillingIntervalPrice will be calculated
            if (remainingPrice.credit <= 0) break

            slotStart = slotEnd
            currentSlot = slotIterator.next()
            slotEnd =
                minOf(
                    rentalPeriodToCalculate.invoicedEnd,
                    currentSlot.end.toInstant()
                )
        }

        // calculate total rental with billing interval max price if cheaper than calculate last billing-interval regular
        if (remainingPrice.credit <= 0)
            return mutableListOf(
                billingIntervalCalculator.calculateTotalRentalWithBillingIntervalMaxPrice(
                    tariff,
                    rentalPeriod
                )
            ).toReceipt(currency = tariff.currency, chargedGoodwill = rentalPeriod.chargedGoodwill)
        else
        // calculate last started billing-interval regular
            billingIntervalCalculator.calculateBillingIntervalPrice(tariff, rentalPeriod)
                ?.let {
                    bill.add(it)
                }

        return bill.toReceipt(currency = tariff.currency, chargedGoodwill = rentalPeriod.chargedGoodwill)
    }
}