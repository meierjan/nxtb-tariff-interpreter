package wtf.meier.tariff.interpreter.model.goodwill.calculator

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.DynamicGoodwill
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

internal class DynamicGoodwillCalculatorTest {

    val goodwillTariff: Tariff = SlotBasedTariff(
        id = TariffId(1),
        currency = Currency.getInstance("EUR"),
        rates = setOf(
            FixedRate(
                id = RateId(1),
                currency = Currency.getInstance("EUR"),
                price = Price(2000)
            )
        ),
        slots = setOf(
            SlotBasedTariff.Slot(
                start = Interval(0, TimeUnit.SECONDS),
                end = null,
                rate = RateId(1)
            ),
        ),
        billingInterval = null,
        goodwill = DynamicGoodwill(deductibleProportionInPercentage = 10f)

    )

    @Test
    fun `test goodWill of duration of 15 min`() {

        val rentalPeriod: RentalPeriod = DynamicGoodwillCalculator.calculateGoodwill(
            dynamicGoodwill = goodwillTariff.goodwill as DynamicGoodwill,
            rentalPeriod = RentalPeriod(rentalEnd = Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(10))),
        )

        rentalPeriod.chargedGoodwill?.let {
            assertThat(
                it.goodwillStart,
                equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(9)))
            )
        }
        assertThat(rentalPeriod.invoicedEnd, equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(9))))
    }
}