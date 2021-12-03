package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.extension.min
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.extension.toReceipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.DayBasedTariff
import java.time.LocalTime.MAX
import java.time.LocalTime.MIN

class DayBasedTariffCalculator(
    private val rateCalculator: RateCalculator = RateCalculator()
) {

    fun calculate(tariff: DayBasedTariff, rentalPeriod: RentalPeriod): Receipt {

        val timeZoneId = tariff.timeZone.toZoneId()

        val startLocalInstant = rentalPeriod.calculatedStart.atZone(timeZoneId)
        val endLocalInstant = rentalPeriod.calculatedEnd.atZone(timeZoneId)


        val positions = mutableListOf<RateCalculator.CalculatedPrice>()
        var currentDay = startLocalInstant
        while (currentDay.isBefore(endLocalInstant)) {

            val relativeStart = currentDay.with(MIN)
            val relativeEnd = min(currentDay.with(MAX), endLocalInstant)


            val startInstant = relativeStart.toInstant()
            val endInstant = relativeEnd.toInstant()

            tariff.rates.forEach {
                val position = rateCalculator.calculate(it, startInstant, endInstant)
                positions.add(position)
            }

            currentDay = currentDay.plusDays(1)
        }

        return positions.toReceipt(tariff.currency)
    }

}


