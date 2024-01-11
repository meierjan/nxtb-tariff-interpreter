package wtf.meier.tariff.interpreter.model.rate.calculator

import kotlinx.datetime.Instant
import wtf.meier.tariff.interpreter.model.Currency
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

internal class FixedRateCalculatorTest {

    private lateinit var fixedRateCalculator: FixedRateCalculator

    @BeforeTest
    fun setUp() {
        fixedRateCalculator = FixedRateCalculator()
    }

    @Test
    fun `Test Fixed Rate Tariff`() {

        val rate = FixedRate(
            id = RateId(100),
            currency = Currency("EUR"),
            price = Price(1000)
        )

        val price = fixedRateCalculator.calculate(
            rate,
            Instant.fromEpochMilliseconds(0),
            Instant.fromEpochMilliseconds(1337)
        )

        assertEquals(price.price.credit, 1000)
    }

}