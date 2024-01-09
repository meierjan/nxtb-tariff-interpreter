package wtf.meier.tariff.interpreter.model.rate.calculator

import kotlinx.datetime.Instant
import wtf.meier.tariff.interpreter.model.Currency
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import wtf.meier.testing.toMillis
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit


class TimeBaseRateCalculatorTest {

    lateinit var calculator: TimeBaseRateCalculator

    @BeforeTest
    fun setup() {
        calculator = TimeBaseRateCalculator()
    }

    private val rate1 = TimeBasedRate(
        id = RateId(1),
        currency = Currency("EUR"),
        interval = 30.minutes,
        basePrice = Price(50),
        pricePerInterval = Price(200),
        maxPrice = Price(1000),
        minPrice = Price(10)
    )

    @Test
    fun `Test calculation for rate1 for 0 minutes`() {
        val start = Instant.fromEpochMilliseconds(0)
        val end = Instant.fromEpochMilliseconds(0)

        val receipt = calculator.calculate(rate1, start, end)

        // 0 seconds = base_price
        assertEquals(receipt.price.credit, 50)
    }


    @Test
    fun `Test calculation for rate1 for 30 minutes`() {
        val start = Instant.fromEpochMilliseconds(0)
        val end = Instant.fromEpochMilliseconds(DurationUnit.MINUTES.toMillis(30))

        val receipt = calculator.calculate(rate1, start, end)

        // seconds + 1 * interval
        assertEquals(receipt.price.credit, 250)
    }

    @Test
    fun `Test calculation for rate1 for 120 minutes`() {
        val start = Instant.fromEpochMilliseconds(0)
        val end = Instant.fromEpochMilliseconds(DurationUnit.MINUTES.toMillis(120))

        val receipt = calculator.calculate(rate1, start, end)

        // base_price (50) + 4 * interval (200)
        assertEquals(receipt.price.credit, 850)
    }

    @Test
    fun `Test calculation for rate1 for 5 hours`() {
        val start = Instant.fromEpochMilliseconds(0)
        val end = Instant.fromEpochMilliseconds(DurationUnit.HOURS.toMillis(5))

        val receipt = calculator.calculate(rate1, start, end)

        // base (50) + 10 * interval (200) > 1000 -> max_price
        assertEquals(receipt.price.credit, 1000)
    }

}