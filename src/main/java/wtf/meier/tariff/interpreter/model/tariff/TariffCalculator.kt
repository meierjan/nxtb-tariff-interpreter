package wtf.meier.tariff.interpreter.model.tariff

import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.tariff.calculator.DayBasedTariffCalculator
import wtf.meier.tariff.interpreter.model.tariff.calculator.SlotBasedTariffCalculator
import wtf.meier.tariff.interpreter.model.tariff.calculator.TimeBasedTariffCalculator
import java.util.*

class TariffCalculator(
    private val slotBasedTariffCalculator: SlotBasedTariffCalculator = SlotBasedTariffCalculator(),
    private val timeBasedTariffCalculator: TimeBasedTariffCalculator = TimeBasedTariffCalculator(),
    private val dayBasedTariffCalculator: DayBasedTariffCalculator = DayBasedTariffCalculator()
) {

    fun calculate(tariff: Tariff, rentalStart: Date, rentalEnd: Date): Receipt =
        when (tariff) {
            is SlotBasedTariff -> slotBasedTariffCalculator.calculate(tariff, rentalStart, rentalEnd)
            is TimeBasedTariff -> timeBasedTariffCalculator.calculate(tariff, rentalStart, rentalEnd)
            is DayBasedTariff -> dayBasedTariffCalculator.calculate(tariff, rentalStart, rentalEnd)
        }

}