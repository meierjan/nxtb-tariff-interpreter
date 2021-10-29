package wtf.meier.tariff.interpreter.model.tariff

import wtf.meier.tariff.interpreter.extension.RentalPeriod
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.tariff.calculator.DayBasedTariffCalculator
import wtf.meier.tariff.interpreter.model.tariff.calculator.SlotBasedTariffCalculator
import wtf.meier.tariff.interpreter.model.tariff.calculator.TimeBasedTariffCalculator
import java.time.Instant

class TariffCalculator(
    private val slotBasedTariffCalculator: SlotBasedTariffCalculator = SlotBasedTariffCalculator(),
    private val timeBasedTariffCalculator: TimeBasedTariffCalculator = TimeBasedTariffCalculator(),
    private val dayBasedTariffCalculator: DayBasedTariffCalculator = DayBasedTariffCalculator()
) {

    fun calculate(tariff: Tariff, rentalPeriod: RentalPeriod): Receipt =
        when (tariff) {
            is SlotBasedTariff -> slotBasedTariffCalculator.calculate(tariff, rentalPeriod)
            is TimeBasedTariff -> timeBasedTariffCalculator.calculate(tariff, rentalPeriod)
            is DayBasedTariff -> dayBasedTariffCalculator.calculate(tariff, rentalPeriod)
        }

}