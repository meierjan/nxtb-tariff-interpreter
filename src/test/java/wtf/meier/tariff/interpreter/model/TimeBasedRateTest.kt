package wtf.meier.tariff.interpreter.model

import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.TimeUnit.HOURS
import java.util.concurrent.TimeUnit.MINUTES

internal class TimeBasedRateTest {

    val rate1 = TimeBasedRate(
        id = RateId(1),
        currency = Currency.getInstance("EUR"),
        interval = Interval(
            timeAmount = 30,
            timeUnit = MINUTES
        ),
        basePrice = Price(50),
        pricePerInterval = Price(200),
        maxPrice = Price(1000),
        minPrice = Price(10)
    )

    @Test
    fun `Test calculation for rate1 for 0 minutes`() {
        val start = Date(0)
        val end = Date(0)

        val price = rate1.calculate(start, end)

        // 0 seconds = base_price
        assert(price.credit == 50L)
    }


    @Test
    fun `Test calculation for rate1 for 30 minutes`() {
        val start = Date(0)
        val end = Date(MINUTES.toMillis(30))

        val price = rate1.calculate(start, end)

        // seconds + 1 * interval
        assert(price.credit == 250L)
    }

    @Test
    fun `Test calculation for rate1 for 120 minutes`() {
        val start = Date(0)
        val end = Date(MINUTES.toMillis(120))

        val price = rate1.calculate(start, end)

        // base_price (50) + 4 * interval (200)
        assert(price.credit == 850L)
    }

    @Test
    fun `Test calculation for rate1 for 5 hours`() {
        val start = Date(0)
        val end = Date(HOURS.toMillis(5))

        val price = rate1.calculate(start, end)

        // base (50) + 10 * interval (200) > 1000 -> max_price
        assert(price.credit == 1000L)
    }


}