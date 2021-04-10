package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.extension.min
import wtf.meier.tariff.interpreter.extension.plus
import wtf.meier.tariff.interpreter.extension.toMillis
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.InvalidTariffFormatException
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import java.util.*

class SlotBasedTariffCalculator(
    private val rateCalculator: RateCalculator = RateCalculator()
) {

    fun calculate(tariff: SlotBasedTariff, start: Date, end: Date): Receipt {
        // order edf
        val sortedSlots = tariff.slots.sortedBy { it.end?.toMillis() ?: Long.MAX_VALUE }
        val rateMap = tariff.rates.associateBy { it.id }

        val finalPrice = mutableListOf<Price>()

        for (slot in sortedSlots) {
            if (slot.matches(start, end)) {

                val rate = rateMap[slot.rate]
                    ?: throw InvalidTariffFormatException("Rate with id ${slot.rate.id} referenced but not defined")

                val slotStart = start + slot.start
                val slotEnd = if (slot.end == null) {
                    end
                } else {
                    min(slotStart + slot.end, end)
                }

                finalPrice.add(rateCalculator.calculate(rate, slotStart, slotEnd))
            }
        }

        return Receipt(
            finalPrice.sumOf { it.credit }.toInt(),
            currency = Currency.getInstance("EUR")
        )
    }
}