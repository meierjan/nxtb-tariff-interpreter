package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff
import java.util.*

class TimeBasedTariffCalculator(
    private val rateCalculator: RateCalculator = RateCalculator()
) {
    fun calculate(tariff: TimeBasedTariff, rentalStart: Date, rentalEnd: Date): Receipt {
        TODO(" not yet implemented")
    }
}