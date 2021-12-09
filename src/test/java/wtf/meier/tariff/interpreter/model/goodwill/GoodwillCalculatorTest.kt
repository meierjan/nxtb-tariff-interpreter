package wtf.meier.tariff.interpreter.model.goodwill

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

internal class GoodwillCalculatorTest {

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
        goodwill = setOf(
            FreeMinutes(duration = Interval(30, TimeUnit.MINUTES)),
            DynamicGoodwill(deductibleProportionInPercentage = 10f),
            StaticGoodwill(duration = Interval(10, TimeUnit.MINUTES))
        )

    )

    @Test
    fun calculateGoodwill() {
        val rentalPeriod: RentalPeriod = GoodwillCalculator.calculateGoodwill(
            tariff = goodwillTariff,
            rentalPeriod = RentalPeriod(rentalEnd = Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(60))),
        )
        assertThat(rentalPeriod.calculatedStart, equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(30))))
        assertThat(rentalPeriod.calculatedEnd, equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(48))))
    }
}