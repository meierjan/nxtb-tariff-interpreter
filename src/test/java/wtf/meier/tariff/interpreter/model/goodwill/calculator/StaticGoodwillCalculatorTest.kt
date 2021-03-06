package wtf.meier.tariff.interpreter.model.goodwill.calculator

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.StaticGoodwill
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

internal class StaticGoodwillCalculatorTest {

    val goodwillTariff: Tariff = SlotBasedTariff(
        id = TariffId(1), currency = Currency.getInstance("EUR"), rates = setOf(
            FixedRate(
                id = RateId(1), currency = Currency.getInstance("EUR"), price = Price(2000)
            )
        ), slots = setOf(
            SlotBasedTariff.Slot(
                start = Interval(0, TimeUnit.SECONDS), end = null, rate = RateId(1)
            ),
        ), billingInterval = null, goodwill = StaticGoodwill(
            duration = Interval(30, TimeUnit.MINUTES)
        )

    )


    @Test
    fun `test goodWill of duration of 15 min`() {

        val rentalPeriod: RentalPeriod = StaticGoodwillcalculator.calculateGoodwill(
            goodwill = goodwillTariff.goodwill as StaticGoodwill,
            rentalPeriod = RentalPeriod(rentalEnd = Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(15)))
        )


        rentalPeriod.chargedGoodwill?.let {
            assertThat(
                it.goodwillEnd,
                equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(15)))
            )
        }
        assertThat(rentalPeriod.invoicedEnd, equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(0))))
    }


    @Test
    fun `test goodWill of duration of 1h 15min`() {

        val rentalPeriod: RentalPeriod = StaticGoodwillcalculator.calculateGoodwill(
            goodwill = goodwillTariff.goodwill as StaticGoodwill,
            rentalPeriod = RentalPeriod(rentalEnd = Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(75))),
        )


        rentalPeriod.chargedGoodwill?.let {
            assertThat(
                it.goodwillStart,
                equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(45)))
            )
        }
        assertThat(rentalPeriod.invoicedEnd, equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(45))))
    }
}