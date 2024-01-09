package wtf.meier.tariff.interpreter.model.tariff

import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.tariff.calculator.SlotBasedTariffCalculator

class TariffCalculator(
    private val slotBasedTariffCalculator: SlotBasedTariffCalculator = SlotBasedTariffCalculator(),
) {

    fun calculate(tariff: Tariff, rentalPeriod: RentalPeriod): Receipt =
            when (tariff) {
                is SlotBasedTariff -> slotBasedTariffCalculator.calculate(tariff, rentalPeriod)
            }
}