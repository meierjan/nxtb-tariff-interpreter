package wtf.meier.tariff.interpreter.model.rate.calculator

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import java.time.Instant
import java.util.*

internal class FixedRateCalculatorTest {

    private lateinit var fixedRateCalculator: FixedRateCalculator

    @BeforeEach
    fun setUp() {
        fixedRateCalculator = FixedRateCalculator()
    }

    @Test
    fun `Test Fixed Rate Tariff`() {

        val rate = FixedRate(
            id = RateId(100),
            currency = Currency.getInstance("EUR"),
            price = Price(1000)
        )

        val price = fixedRateCalculator.calculate(rate, Instant.ofEpochMilli(0), Instant.ofEpochMilli(1337))

        assert(price.price.credit == 1000)
    }

}