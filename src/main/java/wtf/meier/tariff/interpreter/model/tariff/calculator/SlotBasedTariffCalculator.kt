package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.extension.durationMillis
import wtf.meier.tariff.interpreter.extension.plus
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.billingInterval.BillingIntervalCalculator
import wtf.meier.tariff.interpreter.model.extension.minus
import wtf.meier.tariff.interpreter.model.extension.toReceipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.InvalidTariffFormatException
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.util.CyclicListIterator
import java.util.concurrent.TimeUnit

class SlotBasedTariffCalculator(
    private val rateCalculator: RateCalculator = RateCalculator()
) {
    fun calculate(tariff: SlotBasedTariff, rentalPeriod: RentalPeriod): Receipt {
        val positions = mutableListOf<RateCalculator.CalculatedPrice>()
        if (rentalPeriod.invoicedStart >= rentalPeriod.invoicedEnd)
            return positions.toReceipt(
                currency = tariff.currency,
                chargedGoodwill = rentalPeriod.chargedGoodwill
            )

        val rentalPeriodToCalculate =
            BillingIntervalCalculator.calculateRemainingTime(rentalPeriod = rentalPeriod, tariff = tariff)
        var remainingPrice = tariff.billingInterval?.maxPrice ?: Price(Int.MAX_VALUE)

        val rateMap = tariff.rates.associateBy { it.id }
        var slotStart = rentalPeriodToCalculate.invoicedStart

        val filteredSlots = tariff.slots.filter {
            it.matches(
                rentalPeriodToCalculate.invoicedStart,
                rentalPeriodToCalculate.invoicedEnd
            )
        }
        val sortedSlots = filteredSlots.sortedBy { it.end?.durationMillis() ?: Long.MAX_VALUE }
        val slotIterator = CyclicListIterator(sortedSlots)

        var currentSlot = slotIterator.next()

        var slotEnd =
            minOf(
                rentalPeriodToCalculate.invoicedEnd,
                slotStart.plus(currentSlot.end ?: Interval(Integer.MAX_VALUE, TimeUnit.DAYS))
            )

        while (slotStart < rentalPeriodToCalculate.invoicedEnd) {

            val rate = rateMap[currentSlot.rate]
                ?: throw InvalidTariffFormatException("Rate with id ${currentSlot.rate.id} referenced but not defined")

            val receipt = rateCalculator.calculate(
                rate,
                RateCalculator.RatePeriod(rentalStart = slotStart, rentalEnd = slotEnd)
            )
            positions.add(receipt)
            remainingPrice -= receipt.price
            if (remainingPrice.credit <= 0) break

            slotStart = slotEnd
            currentSlot = slotIterator.next()
            slotEnd =
                minOf(
                    rentalPeriodToCalculate.invoicedEnd,
                    slotStart.plus(currentSlot.duration)
                )
        }

        if (remainingPrice.credit <= 0)
            return mutableListOf(
                BillingIntervalCalculator.calculateWholeRentalWithBillingIntervalMaxPrice(
                    tariff,
                    rentalPeriod
                )
            ).toReceipt(currency = tariff.currency, chargedGoodwill = rentalPeriod.chargedGoodwill)
        else
            BillingIntervalCalculator.calculateBillingIntervalPrice(tariff, rentalPeriod)
                ?.let {
                    positions.add(it)
                }
        return positions.toReceipt(currency = tariff.currency, chargedGoodwill = rentalPeriod.chargedGoodwill)
    }
}