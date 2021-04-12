package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.extension.min
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.DayBasedTariff
import java.time.Instant
import java.time.LocalTime.MAX
import java.time.LocalTime.MIN
import java.util.*

class DayBasedTariffCalculator(
    private val rateCalculator: RateCalculator = RateCalculator()
) {

    fun calculate(tariff: DayBasedTariff, rentalStart: Instant, rentalEnd: Instant): Receipt {

        val timeZoneId = tariff.timeZone.toZoneId()

        val startLocalInstant = rentalStart.atZone(timeZoneId)
        val endLocalInstant = rentalEnd.atZone(timeZoneId)


        var finalPrice = 0L
        var currentDay = startLocalInstant
        while (currentDay.isBefore(endLocalInstant)) {

            val relativeStart = currentDay.with(MIN)
            val relativeEnd = min(currentDay.with(MAX), endLocalInstant)


            val startInstant = relativeStart.toInstant()
            val endInstant = relativeEnd.toInstant()

            tariff.rates.forEach {
                val price = rateCalculator.calculate(it, startInstant, endInstant).credit
                finalPrice += price
            }

            currentDay = currentDay.plusDays(1)


        }

        return Receipt(finalPrice.toInt(), Currency.getInstance("EUR"), emptyList())

    }

}


