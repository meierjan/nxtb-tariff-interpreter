package wtf.meier.tariff.interpreter.model.goodwill.calculator

import wtf.meier.tariff.interpreter.extension.durationMillis
import wtf.meier.tariff.interpreter.extension.minus
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.ChargedGoodwill
import wtf.meier.tariff.interpreter.model.goodwill.DynamicGoodwill
import java.util.concurrent.TimeUnit
import kotlin.math.ceil

object DynamicGoodwillCalculator {
    fun calculateGoodwill(
        dynamicGoodwill: DynamicGoodwill,
        rentalPeriod: RentalPeriod,
    ): RentalPeriod {
        val calculatedGoodwill = Interval(
            ceil(rentalPeriod.duration.durationMillis() * dynamicGoodwill.deductibleProportionInPercentage / 100).toInt(),
            TimeUnit.MILLISECONDS
        )

        val chargedGoodwill = ChargedGoodwill(
            goodwillStart = rentalPeriod.invoicedEnd - calculatedGoodwill,
            goodwillEnd = rentalPeriod.invoicedEnd,
            description = "${calculatedGoodwill.timeUnit.toMinutes(calculatedGoodwill.timeAmount.toLong())} minutes were deducted from your travel time as a gesture of goodwill"
        )

        return rentalPeriod.copy(
            invoicedEnd = chargedGoodwill.goodwillStart,
            chargedGoodwill = chargedGoodwill
        )
    }

}
