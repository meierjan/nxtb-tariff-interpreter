package wtf.meier.tariff.interpreter.model.tariff

import wtf.meier.tariff.interpreter.extension.RentalPeriod
import wtf.meier.tariff.interpreter.extension.minus
import wtf.meier.tariff.interpreter.extension.plus
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import java.time.Instant
import java.util.concurrent.TimeUnit

object FreeMinutesCalculator {

    fun calculateFreeMinutes(tariff: Tariff, rentalPeriod: RentalPeriod): RentalPeriod {

        var period = deductGoodWillMinutes(tariff, rentalPeriod)
        period = deductFreeMinutes(tariff, period)

        return period
    }


    private fun deductGoodWillMinutes(
        tariff: Tariff,
        rentalPeriod: RentalPeriod
    ): RentalPeriod {

        if (!tariff.fairTariff) return rentalPeriod

        val durationInSeconds: Long = rentalPeriod.calculateDuration().epochSecond

        val freeMinutes: Long = if (durationInSeconds <= 60 * 10) {
            return rentalPeriod
        } else if (durationInSeconds > 60 * 10 && durationInSeconds <= 60 * 60) {
            5
        } else if (durationInSeconds > 60 * 60 && durationInSeconds <= 60 * 60 * 24) {
            10
        } else {
            45
        }

        val goodWillPosition = RateCalculator.CalculatedPrice(
            price = Price(0),
            currency = tariff.rates.first().currency,
            positionStart = rentalPeriod.calculatedEnd - Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(freeMinutes)),
            positionEnd = rentalPeriod.calculatedEnd,
            description = "$freeMinutes minutes were deducted from your travel time as a gesture of goodwill"
        )

        rentalPeriod.calculatedEnd = rentalPeriod.calculatedEnd - Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(freeMinutes))
        rentalPeriod.positions.add(goodWillPosition)

        return rentalPeriod
    }


    private fun deductFreeMinutes(tariff: Tariff, rentalPeriod: RentalPeriod): RentalPeriod {
        if (tariff.freeSeconds <= 0) return rentalPeriod
        val durationInSeconds: Long = rentalPeriod.calculateDuration().epochSecond
        val freeSeconds = minOf(durationInSeconds.toInt(), tariff.freeSeconds)

        val freeMinutesPosition = RateCalculator.CalculatedPrice(
            price = Price(0),
            currency = tariff.rates.first().currency,
            positionStart = rentalPeriod.calculatedStart,
            positionEnd = rentalPeriod.calculatedStart + Interval(freeSeconds, TimeUnit.SECONDS),
            description = "${TimeUnit.SECONDS.toMinutes(freeSeconds.toLong())} minutes were deducted from your travel time as a gesture of goodwill"
        )

        rentalPeriod.calculatedStart = rentalPeriod.calculatedStart + Interval(freeSeconds, TimeUnit.SECONDS)
        rentalPeriod.positions.add(freeMinutesPosition)

        return rentalPeriod
    }


}
