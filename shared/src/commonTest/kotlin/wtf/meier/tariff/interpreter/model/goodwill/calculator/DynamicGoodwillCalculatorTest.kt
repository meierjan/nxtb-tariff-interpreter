package wtf.meier.tariff.interpreter.model.goodwill.calculator

import kotlinx.datetime.Instant
import wtf.meier.tariff.interpreter.model.Currency
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.DynamicGoodwill
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.testing.toSeconds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

internal class DynamicGoodwillCalculatorTest {

    private val goodwillTariff: Tariff = SlotBasedTariff(
        id = TariffId(1),
        currency = Currency("EUR"),
        rates = setOf(
            FixedRate(
                id = RateId(1),
                currency = Currency("EUR"),
                price = Price(2000)
            )
        ),
        slots = setOf(
            SlotBasedTariff.Slot(
                start = 0.seconds,
                end = null,
                rate = RateId(1)
            ),
        ),
        billingInterval = null,
        goodwill = DynamicGoodwill(deductibleProportionInPercentage = 10.0)

    )

    @Test
    fun `test goodWill of duration of 15 min`() {

        val rentalPeriod: RentalPeriod = DynamicGoodwillCalculator.calculateGoodwill(
            dynamicGoodwill = goodwillTariff.goodwill as DynamicGoodwill,
            rentalPeriod = RentalPeriod(
                rentalEnd = Instant.fromEpochSeconds(
                    DurationUnit.MINUTES.toSeconds(
                        10
                    )
                )
            ),
        )

        rentalPeriod.chargedGoodwill?.let {
            assertEquals(
                it.goodwillStart,
                Instant.fromEpochSeconds(9.minutes.inWholeSeconds)
            )
        }
        assertEquals(
            rentalPeriod.invoicedEnd,
            Instant.fromEpochSeconds(9.minutes.inWholeSeconds)
        )
    }
}