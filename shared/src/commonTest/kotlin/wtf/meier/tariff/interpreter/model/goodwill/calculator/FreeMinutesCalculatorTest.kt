package wtf.meier.tariff.interpreter.model.goodwill.calculator


import kotlinx.datetime.Instant
import wtf.meier.tariff.interpreter.model.Currency
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.FreeMinutes
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

internal class FreeMinutesCalculatorTest {

    val goodwillTariff: Tariff = SlotBasedTariff(
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
        goodwill = FreeMinutes(
            duration = 30.minutes
        )

    )


    @Test
    fun `test goodWill of duration of 15 min`() {

        val rentalPeriod: RentalPeriod = FreeMinutesCalculator.calculateGoodwill(
            freeMinutes = goodwillTariff.goodwill as FreeMinutes,
            rentalPeriod = RentalPeriod(
                rentalEnd = Instant.fromEpochSeconds(
                    DurationUnit.MINUTES.toSeconds(
                        15
                    )
                )
            ),
        )

        rentalPeriod.chargedGoodwill?.let {
            assertEquals(
                it.goodwillEnd,
                Instant.fromEpochSeconds(15.minutes.inWholeSeconds)
            )
        }
        assertEquals(
            rentalPeriod.invoicedEnd,
            Instant.fromEpochSeconds(15.minutes.inWholeSeconds)
        )
    }


    @Test
    fun `test goodWill of duration of 1h 15min`() {

        val rentalPeriod: RentalPeriod = FreeMinutesCalculator.calculateGoodwill(
            freeMinutes = goodwillTariff.goodwill as FreeMinutes,
            rentalPeriod = RentalPeriod(
                rentalEnd = Instant.fromEpochSeconds(
                    DurationUnit.MINUTES.toSeconds(
                        75
                    )
                )
            ),
        )

        assertEquals(
            rentalPeriod.chargedGoodwill!!.goodwillEnd,
            Instant.fromEpochSeconds(30.minutes.inWholeSeconds)
        )
        assertEquals(
            rentalPeriod.invoicedStart,
            Instant.fromEpochSeconds(30.minutes.inWholeSeconds)
        )
    }
}