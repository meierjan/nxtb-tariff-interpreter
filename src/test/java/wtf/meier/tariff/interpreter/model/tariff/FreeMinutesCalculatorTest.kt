package wtf.meier.tariff.interpreter.model.tariff

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.extension.RentalPeriod
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

internal class FreeMinutesCalculatorTest {

    private val fairTariff: Tariff = SlotBasedTariff(
        id = TariffId(1),
        freeSeconds = 0,
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
        fairTariff = true

    )


    @Test
    fun `test goodWill of duration of 15 min`() {

        val rentalPeriod: RentalPeriod = FreeMinutesCalculator.calculateFreeMinutes(
            tariff = fairTariff,
            RentalPeriod(rentalEnd = Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(15)))
        )

        assertThat(
            rentalPeriod.positions.first().positionStart,
            equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(10)))
        )
        assertThat(rentalPeriod.calculatedEnd, equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(10))))


    }

    @Test
    fun `test goodWill of duration of 80 min`() {

        val rentalPeriod: RentalPeriod = FreeMinutesCalculator.calculateFreeMinutes(
            tariff = fairTariff,
            RentalPeriod(rentalEnd = Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(80)))
        )

        assertThat(
            rentalPeriod.positions.first().positionStart,
            equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(70)))
        )
        assertThat(rentalPeriod.calculatedEnd, equalTo(Instant.ofEpochSecond(TimeUnit.MINUTES.toSeconds(70))))
    }

    @Test
    fun `test goodWill of duration of 25 h`() {

        val rentalPeriod: RentalPeriod = FreeMinutesCalculator.calculateFreeMinutes(
            tariff = fairTariff,
            RentalPeriod(rentalEnd = Instant.ofEpochSecond(TimeUnit.HOURS.toSeconds(25)))
        )

        assertThat(
            rentalPeriod.positions.first().positionStart,
            equalTo(Instant.ofEpochSecond(TimeUnit.HOURS.toSeconds(25) - TimeUnit.MINUTES.toSeconds(45)))
        )
        assertThat(
            rentalPeriod.calculatedEnd,
            equalTo(Instant.ofEpochSecond(TimeUnit.HOURS.toSeconds(25) - TimeUnit.MINUTES.toSeconds(45)))
        )
    }

}