package wtf.meier.tariff.interpreter.model.goodwill.calculator

import wtf.meier.tariff.interpreter.extension.durationMillis
import wtf.meier.tariff.interpreter.extension.minus
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.DynamicGoodwill
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

object DynamicGoodwillCalculator {
    fun calculateGoodwill(
        dynamicGoodwill: DynamicGoodwill,
        rentalPeriod: RentalPeriod,
        currency: Currency
    ): RentalPeriod {
        val calculatedGoodwill = Interval(
            ceil(rentalPeriod.duration.durationMillis() * dynamicGoodwill.deductibleProportionInPercentage/100).toInt(),
            TimeUnit.MILLISECONDS
        )

        val dynamicGoodwillPosition: RateCalculator.CalculatedPrice = RateCalculator.CalculatedPrice(
            price = Price(0),
            currency = currency,
            calculationStart = rentalPeriod.calculatedEnd - calculatedGoodwill,
            calculationEnd = rentalPeriod.calculatedEnd,
            description = "${calculatedGoodwill.timeUnit.toMinutes(calculatedGoodwill.timeAmount.toLong())}$ minutes were deducted from your travel time as a gesture of goodwill"
        )

        rentalPeriod.positions.add(dynamicGoodwillPosition)

        return rentalPeriod.copy(calculatedEnd = rentalPeriod.calculatedEnd - calculatedGoodwill)
    }

}
