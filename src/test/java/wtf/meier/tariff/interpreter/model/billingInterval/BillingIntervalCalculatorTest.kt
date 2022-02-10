package wtf.meier.tariff.interpreter.model.billingInterval

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

internal class BillingIntervalCalculatorTest {

    private val tariff = SlotBasedTariff(
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
                end = Interval(2, TimeUnit.HOURS),
                rate = RateId(1)
            ),
            SlotBasedTariff.Slot(
                start = Interval(2, TimeUnit.HOURS),
                end = null,
                RateId(1)
            )
        ),
        billingInterval = BillingInterval(duration = Interval(1, TimeUnit.DAYS), maxPrice = Price(1000))
    )

    private val rentalPeriod =
        RentalPeriod(
            rentalStart = Instant.ofEpochMilli(0),
            rentalEnd = Instant.ofEpochMilli(TimeUnit.DAYS.toMillis(4) + 100)
        )

    @Test
    fun calculateBillingIntervalPrice() {
        val result = BillingIntervalCalculator.calculateBillingIntervalPrice(tariff, rentalPeriod)

        assertThat(result!!.price, equalTo(Price(4000)))
    }

    @Test
    fun calculateRemainingTime() {
        val result = BillingIntervalCalculator.calculateRemainingTime(tariff, rentalPeriod)

        assertThat(result, equalTo(rentalPeriod.copy(invoicedStart = Instant.ofEpochMilli(TimeUnit.DAYS.toMillis(4)))))
    }
}