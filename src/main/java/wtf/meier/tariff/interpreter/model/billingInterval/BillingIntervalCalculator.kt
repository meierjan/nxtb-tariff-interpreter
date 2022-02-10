package wtf.meier.tariff.interpreter.model.billingInterval

import wtf.meier.tariff.interpreter.extension.*
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.extension.times
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.tariff.Tariff

object BillingIntervalCalculator {

    fun calculateRemainingTime(tariff: Tariff, rentalPeriod: RentalPeriod): RentalPeriod {
        if (tariff.billingInterval == null) return rentalPeriod

        val remainingTime = rentalPeriod.duration % tariff.billingInterval!!.duration

        return rentalPeriod.copy(invoicedStart = rentalPeriod.invoicedEnd.minusMillis(remainingTime.durationMillis()))
    }

    fun calculateBillingIntervalPrice(
        tariff: Tariff, rentalPeriod: RentalPeriod
    ): RateCalculator.CalculatedPrice? {
        if (tariff.billingInterval == null) return null

        val numberOfWholeBillingInterval = rentalPeriod.duration / tariff.billingInterval!!.duration

        if (numberOfWholeBillingInterval < 1) return null

        return RateCalculator.CalculatedPrice(
            price = tariff.billingInterval!!.maxPrice * numberOfWholeBillingInterval.toInt(),
            currency = tariff.currency,
            description = "The billingInterval was finished $numberOfWholeBillingInterval times",
            calculationStart = rentalPeriod.invoicedStart,
            calculationEnd = rentalPeriod.invoicedStart.plusMillis((tariff.billingInterval!!.duration * numberOfWholeBillingInterval).durationMillis())
        )
    }

    fun calculateWholeRentalWithBillingIntervalMaxPrice(
        tariff: Tariff, rentalPeriod: RentalPeriod
    ): RateCalculator.CalculatedPrice {

        val numberOfWholeBillingInterval = rentalPeriod.duration.ceilDiv(tariff.billingInterval!!.duration)

        return RateCalculator.CalculatedPrice(
            price = tariff.billingInterval!!.maxPrice * numberOfWholeBillingInterval.toInt(),
            currency = tariff.currency,
            description = "The billingInterval was finished $numberOfWholeBillingInterval times",
            calculationStart = rentalPeriod.invoicedStart,
            calculationEnd = rentalPeriod.invoicedStart.plusMillis((tariff.billingInterval!!.duration * numberOfWholeBillingInterval).durationMillis())
        )
    }

}