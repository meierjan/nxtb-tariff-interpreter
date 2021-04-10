package wtf.meier.tariff.interpreter.model.tariff

import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.tariff.calculator.SlotBasedTariffCalculator
import wtf.meier.tariff.interpreter.model.tariff.calculator.TimeBasedTariffCalculator
import java.util.*

class TariffCalculator(
    private val slotBasedTariffCalculator: SlotBasedTariffCalculator = SlotBasedTariffCalculator(),
    private val timeBasedTariffCalculator: TimeBasedTariffCalculator = TimeBasedTariffCalculator()
) {

    fun calculate(tariff: Tariff, start: Date, end: Date): Receipt =
        when (tariff) {
            is SlotBasedTariff -> slotBasedTariffCalculator.calculate(tariff, start, end)
            is TimeBasedTariff -> timeBasedTariffCalculator.calculate(tariff, start, end)
        }

}