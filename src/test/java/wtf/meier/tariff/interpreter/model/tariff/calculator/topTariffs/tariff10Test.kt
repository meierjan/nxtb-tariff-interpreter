package wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.tariff.interpreter.model.tariff.calculator.SlotBasedTariffCalculator
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

class tariff10Test {

    private lateinit var rateCalculator: RateCalculator
    private lateinit var calculator: SlotBasedTariffCalculator

    @BeforeEach
    fun setup() {
        rateCalculator = RateCalculator()
        calculator = SlotBasedTariffCalculator()
    }

    private val tariff10 = SlotBasedTariff(
        id = TariffId(1),
        freeSeconds = 0,
        rates = setOf(
            FixedRate(
                id = RateId(1),
                currency = Currency.getInstance("EUR"),
                price = Price(0)
            ),
        ),
        slots = setOf(
            SlotBasedTariff.Slot(
                start = Interval(0, TimeUnit.MINUTES),
                end = null,
                RateId(1)
            ),
        ),
        billingInterval = null
    )

    @Test
    fun `test tariff 10 `() {
        val receipt = calculator.calculate(
            tariff = tariff10,
            rentalStart = Instant.ofEpochMilli(TimeUnit.SECONDS.toMillis(0)),
            rentalEnd = Instant.ofEpochMilli(TimeUnit.SECONDS.toMillis(10000))
        )

        assertThat(receipt.price, equalTo(0))

    }
}