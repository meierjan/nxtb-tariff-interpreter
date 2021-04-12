package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.extension.durationMillis
import wtf.meier.tariff.interpreter.extension.min
import wtf.meier.tariff.interpreter.extension.plus
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.extension.toReceipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.InvalidTariffFormatException
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import java.time.Instant

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

                val rate = rateMap[slot.rate]
                    ?: throw InvalidTariffFormatException("Rate with id ${slot.rate.id} referenced but not defined")

                val slotStart = rentalStart + slot.start
                val slotEnd = if (slot.end == null) {
                    rentalEnd
                } else {
                    min(slotStart + slot.end, rentalEnd)
                }

                positions.add(rateCalculator.calculate(rate, slotStart, slotEnd))
            }
        }

        return positions.toReceipt()
    }
}