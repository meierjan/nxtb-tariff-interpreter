package wtf.meier.tariff.interpreter

import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.extension.minus
import wtf.meier.tariff.interpreter.extension.plus
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import java.time.Instant
import java.util.concurrent.TimeUnit

object FreeMinutesCalculator:IFreeMinutesCalculator {

    override fun calculateFreeMinutes(tariff: Tariff, rentalPeriod: RentalPeriod): RentalPeriod {

        var period = deductGoodWillMinutes(tariff, rentalPeriod)
        period = deductFreeMinutes(tariff, period)

        return period
    }

    private fun deductGoodWillMinutes(
        tariff: Tariff,
        rentalPeriod: RentalPeriod
    ): RentalPeriod {

        if (!tariff.fairTariff) return rentalPeriod

        val durationInSeconds: Long = rentalPeriod.duration.epochSecond

        val freeMinutes: Long = if (durationInSeconds <= 60 * 10) {
            return rentalPeriod
        } else if (durationInSeconds <= 60 * 60) {
            5
        } else if (durationInSeconds <= 60 * 60 * 24) {
            10
        } else {
            45
        }

        val goodWillPosition = RateCalculator.CalculatedPrice(
            price = Price(0),
            currency = tariff.rates.first().currency,
            calculationStart = rentalPeriod.calculatedEnd - Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(freeMinutes)),
            calculationEnd = rentalPeriod.calculatedEnd,
            description = "$freeMinutes minutes were deducted from your travel time as a gesture of goodwill"
        )

        rentalPeriod.positions.add(goodWillPosition)

        return rentalPeriod.copy(calculatedEnd = rentalPeriod.calculatedEnd - Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(freeMinutes)))
    }

    private fun deductFreeMinutes(tariff: Tariff, rentalPeriod: RentalPeriod): RentalPeriod {
        if (tariff.freeSeconds <= 0) return rentalPeriod
        val durationInSeconds: Long = rentalPeriod.duration.epochSecond
        val freeSeconds = minOf(durationInSeconds.toInt(), tariff.freeSeconds)

        val freeMinutesPosition = RateCalculator.CalculatedPrice(
            price = Price(0),
            currency = tariff.rates.first().currency,
            calculationStart = rentalPeriod.calculatedStart,
            calculationEnd = rentalPeriod.calculatedStart + Interval(freeSeconds, TimeUnit.SECONDS),
            description = "${TimeUnit.SECONDS.toMinutes(freeSeconds.toLong())} minutes were deducted from your travel time as a gesture of goodwill"
        )

        rentalPeriod.positions.add(freeMinutesPosition)

        return rentalPeriod.copy(calculatedStart = rentalPeriod.calculatedStart + Interval(freeSeconds, TimeUnit.SECONDS))
    }
}
