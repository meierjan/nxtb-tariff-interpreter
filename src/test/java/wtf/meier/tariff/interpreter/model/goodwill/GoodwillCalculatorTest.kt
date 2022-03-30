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
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

internal class GoodwillCalculatorTest {

    private val goodwillTariff = SlotBasedTariff(
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
                rate = RateId(1)
            ),
        ),
    )

    private val staticGoodwill = StaticGoodwill(Interval(30, TimeUnit.MINUTES))
    private val dynamicGoodwill = DynamicGoodwill(10f)
    private val freeMinutes = FreeMinutes(Interval(30, TimeUnit.MINUTES))


    @Test
    fun calculateStaticGoodwill() {
        goodwillTariff
        val rentalPeriod: RentalPeriod = GoodwillCalculator.calculateGoodwill(
            tariff = goodwillTariff.copy(goodwill = staticGoodwill),
            rentalPeriod = RentalPeriod(
                rentalEnd = Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(60))
            ),
        )
        assertThat(rentalPeriod.invoicedStart, equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(0))))
        assertThat(rentalPeriod.invoicedEnd, equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(30))))
    }

    @Test
    fun calculateDynamicGoodwill() {
        goodwillTariff
        val rentalPeriod: RentalPeriod = GoodwillCalculator.calculateGoodwill(
            tariff = goodwillTariff.copy(goodwill = dynamicGoodwill),
            rentalPeriod = RentalPeriod(rentalEnd = Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(60))),
        )
        assertThat(rentalPeriod.invoicedStart, equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(0))))
        assertThat(rentalPeriod.invoicedEnd, equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(54))))
    }

    @Test
    fun calculateFreeMinutes() {
        goodwillTariff
        val rentalPeriod: RentalPeriod = GoodwillCalculator.calculateGoodwill(
            tariff = goodwillTariff.copy(goodwill = freeMinutes),
            rentalPeriod = RentalPeriod(rentalEnd = Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(60))),
        )
        assertThat(rentalPeriod.invoicedStart, equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(30))))
        assertThat(rentalPeriod.invoicedEnd, equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(60))))
    }
}