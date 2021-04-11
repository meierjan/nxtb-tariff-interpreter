package wtf.meier.tariff.interpreter.model.tariff.calculator

import wtf.meier.tariff.interpreter.extension.min
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.DayBasedTariff
import java.time.LocalTime.MAX
import java.time.LocalTime.MIN
import java.util.*

class DayBasedTariffCalculator(
    private val rateCalculator: RateCalculator = RateCalculator()
) {

    fun calculate(tariff: DayBasedTariff, rentalStart: Date, rentalEnd: Date): Receipt {

        val timeZoneId = tariff.timeZone.toZoneId()

        val startLocalDate = rentalStart.toInstant().atZone(timeZoneId)
        val endLocalDate = rentalEnd.toInstant().atZone(timeZoneId)


        var finalPrice = 0L
        var currentDay = startLocalDate
        while (currentDay.isBefore(endLocalDate)) {

            val relativeStart = currentDay.with(MIN)
            val relativeEnd = min(currentDay.with(MAX), endLocalDate)


            val startDate = Date(relativeStart.toInstant().epochSecond)
            val endDate = Date(relativeEnd.toInstant().epochSecond)

            tariff.rates.forEach {
                val price = rateCalculator.calculate(it, startDate, endDate).credit
                finalPrice += price
            }

            currentDay = currentDay.plusDays(1)


        }

        return Receipt(finalPrice.toInt(), Currency.getInstance("EUR"), emptyList())

    }

}


