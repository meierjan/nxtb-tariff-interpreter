package wtf.meier.tariff.interpreter.model.goodwill

import kotlinx.datetime.Instant
import wtf.meier.tariff.interpreter.model.Currency
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.testing.toSeconds
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

internal class GoodwillCalculatorTest {

    private val goodwillTariff = SlotBasedTariff(
        id = TariffId(1), currency = Currency("EUR"), rates = setOf(
            FixedRate(
                id = RateId(1), currency = Currency("EUR"), price = Price(2000)
            )
        ), slots = setOf(
            SlotBasedTariff.Slot(
                start = 0.seconds, end = null, rate = RateId(1)
            ),
        ), billingInterval = null
    )

    private val staticGoodwill = StaticGoodwill(30.minutes)
    private val dynamicGoodwill = DynamicGoodwill(10.0)
    private val freeMinutes = FreeMinutes(30.minutes)


    @Test
    fun calculateStaticGoodwill() {
        goodwillTariff
        val rentalPeriod: RentalPeriod = GoodwillCalculator.calculateGoodwill(
            tariff = goodwillTariff.copy(goodwill = staticGoodwill),
            rentalPeriod = RentalPeriod(
                rentalEnd = Instant.fromEpochSeconds(60.minutes.inWholeSeconds)
            ),
        )
        assertEquals(rentalPeriod.invoicedStart, Instant.fromEpochMilliseconds(0))
        assertEquals(
            Instant.fromEpochSeconds(30.minutes.inWholeSeconds),
            rentalPeriod.invoicedEnd,
        )
    }

    @Test
    fun calculateDynamicGoodwill() {
        goodwillTariff
        val rentalPeriod: RentalPeriod = GoodwillCalculator.calculateGoodwill(
            tariff = goodwillTariff.copy(goodwill = dynamicGoodwill),
            rentalPeriod = RentalPeriod(
                rentalEnd = Instant.fromEpochSeconds(
                    DurationUnit.MINUTES.toSeconds(
                        60
                    )
                )
            ),
        )
        assertEquals(
            rentalPeriod.invoicedStart, Instant.fromEpochSeconds(0.minutes.inWholeSeconds)
        )
        assertEquals(
            rentalPeriod.invoicedEnd, Instant.fromEpochSeconds(54.minutes.inWholeSeconds)
        )
    }

    @Test
    fun calculateFreeMinutes() {
        goodwillTariff
        val rentalPeriod: RentalPeriod = GoodwillCalculator.calculateGoodwill(
            tariff = goodwillTariff.copy(goodwill = freeMinutes),
            rentalPeriod = RentalPeriod(
                rentalEnd = Instant.fromEpochSeconds(60.minutes.inWholeSeconds),
            ),
        )
        assertEquals(
            Instant.fromEpochSeconds(30.minutes.inWholeSeconds),
            rentalPeriod.invoicedStart,
        )
        assertEquals(
            Instant.fromEpochSeconds(60.minutes.inWholeSeconds),
            rentalPeriod.invoicedEnd,
        )
    }
}