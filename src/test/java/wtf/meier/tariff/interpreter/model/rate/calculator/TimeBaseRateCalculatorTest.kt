package wtf.meier.tariff.interpreter.model.rate.calculator

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

class TimeBaseRateCalculatorTest {

    lateinit var calculator: TimeBaseRateCalculator

    @BeforeEach
    fun setup() {
        calculator = TimeBaseRateCalculator()
    }

    private val rate1 = TimeBasedRate(
        id = RateId(1),
        currency = Currency.getInstance("EUR"),
        interval = Interval(
            timeAmount = 30,
            timeUnit = TimeUnit.MINUTES
        ),
        basePrice = Price(50),
        pricePerInterval = Price(200),
        maxPrice = Price(1000),
        minPrice = Price(10)
    )

    @Test
    fun `Test calculation for rate1 for 0 minutes`() {
        val start = Instant.ofEpochMilli(0)
        val end = Instant.ofEpochMilli(0)

        val receipt = calculator.calculate(rate1, start, end)

        // 0 seconds = base_price
        assertThat(receipt.price.credit, equalTo(50))
    }


    @Test
    fun `Test calculation for rate1 for 30 minutes`() {
        val start = Instant.ofEpochMilli(0)
        val end = Instant.ofEpochMilli(TimeUnit.MINUTES.toMillis(30))

        val receipt = calculator.calculate(rate1, start, end)

        // seconds + 1 * interval
        assertThat(receipt.price.credit, equalTo(250))
    }

    @Test
    fun `Test calculation for rate1 for 120 minutes`() {
        val start = Instant.ofEpochMilli(0)
        val end = Instant.ofEpochMilli(TimeUnit.MINUTES.toMillis(120))

        val receipt = calculator.calculate(rate1, start, end)

        // base_price (50) + 4 * interval (200)
        assertThat(receipt.price.credit, equalTo(850))
    }

    @Test
    fun `Test calculation for rate1 for 5 hours`() {
        val start = Instant.ofEpochMilli(0)
        val end = Instant.ofEpochMilli(TimeUnit.HOURS.toMillis(5))

        val receipt = calculator.calculate(rate1, start, end)

        // base (50) + 10 * interval (200) > 1000 -> max_price
        assertThat(receipt.price.credit, equalTo(1000))
    }

}