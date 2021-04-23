package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.extension.durationMillis
import wtf.meier.tariff.interpreter.extension.min
import wtf.meier.tariff.interpreter.extension.plus
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.extension.toReceipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.InvalidTariffFormatException
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import java.time.Instant
import java.util.concurrent.TimeUnit

class SlotBasedTariffCalculator(
    private val rateCalculator: RateCalculator = RateCalculator()
) {

    fun calculate(tariff: SlotBasedTariff, rentalStart: Instant, rentalEnd: Instant): Receipt {
        // order edf
        val sortedSlots = tariff.slots.sortedBy { it.end?.durationMillis() ?: Long.MAX_VALUE }
        val rateMap = tariff.rates.associateBy { it.id }

        val positions = mutableListOf<RateCalculator.CalculatedPrice>()

        for (slot in sortedSlots) {
            if (slot.matches(rentalStart, rentalEnd)) {

                var currentBillingStart = rentalStart
                var currentBillingEnd = if (tariff.billingInterval != null) {
                    rentalStart.plus(tariff.billingInterval)
                } else {
                    rentalEnd
                }

                while (currentBillingStart < rentalEnd) {

                    val rate = rateMap[slot.rate]
                        ?: throw InvalidTariffFormatException("Rate with id ${slot.rate.id} referenced but not defined")

                    val slotStart = currentBillingStart + slot.start
                    val slotEnd = if (slot.end == null) {
                        min(rentalEnd, currentBillingEnd)
                    } else {
                        minOf(slotStart + slot.end, rentalEnd, currentBillingEnd)
                    }

                    positions.add(rateCalculator.calculate(rate, slotStart, slotEnd))

                    currentBillingStart = currentBillingEnd
                    currentBillingEnd = if (tariff.billingInterval != null) {
                        currentBillingStart.plus(tariff.billingInterval!!)
                    } else {
                        rentalEnd
                    }

                }
            }
        }

        return positions.toReceipt()
    }
}