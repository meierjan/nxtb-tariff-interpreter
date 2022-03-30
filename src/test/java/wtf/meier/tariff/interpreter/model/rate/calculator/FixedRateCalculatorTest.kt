package wtf.meier.tariff.interpreter.model.rate.calculator

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.rate.RateId
import java.time.Instant
import java.util.*

internal class FixedRateCalculatorTest {

    private lateinit var fixedRateCalculator: FixedRateCalculator
    private val rate1 = FixedRate(
        id = RateId(100),
        currency = Currency.getInstance("EUR"),
        price = Price(1000)
    )

    @BeforeEach
    fun setUp() {
        fixedRateCalculator = FixedRateCalculator()
    }

    @Test
    fun `Test Fixed Rate Tariff`() {

        val rate = rate1

        val price = fixedRateCalculator.calculate(
            rate,
            RateCalculator.RatePeriod(Instant.ofEpochMilli(0), Instant.ofEpochMilli(1337))
        )

        assertThat(price.price.credit, equalTo(1000))
    }


}