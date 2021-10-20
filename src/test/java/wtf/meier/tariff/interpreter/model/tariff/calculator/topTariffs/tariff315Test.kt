package wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs

import com.google.gson.Gson
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.tariff.interpreter.model.tariff.calculator.SlotBasedTariffCalculator
import wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs.testData.PriceChart
import java.io.FileReader
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

class tariff315Test {
    private lateinit var rateCalculator: RateCalculator
    private lateinit var calculator: SlotBasedTariffCalculator

    @BeforeEach
    fun setup() {
        rateCalculator = RateCalculator()
        calculator = SlotBasedTariffCalculator()
    }

    private val tariff315 = SlotBasedTariff(
        id = TariffId(1),
        freeSeconds = TimeUnit.MINUTES.toSeconds(25).toInt(),
        rates = setOf(
            FixedRate(
                id = RateId(1),
                currency = Currency.getInstance("PLN"),
                price = Price(100)
            ),
            FixedRate(
                id = RateId(2),
                currency = Currency.getInstance("PLN"),
                price = Price(300)
            ),
            TimeBasedRate(
                id = RateId(3),
                currency = Currency.getInstance("PLN"),
                interval = Interval(60, TimeUnit.MINUTES),
                basePrice = Price(0),
                maxPrice = Price(Integer.MAX_VALUE),
                minPrice = Price(0),
                pricePerInterval = Price(500),
            ),
            FixedRate(
                id = RateId(4),
                currency = Currency.getInstance("PLN"),
                price = Price(30000)
            ),
        ),
        slots = setOf(
            SlotBasedTariff.Slot(
                start = Interval(0, TimeUnit.MINUTES),
                end = Interval(1, TimeUnit.HOURS),
                RateId(1)
            ),
            SlotBasedTariff.Slot(
                start = Interval(1, TimeUnit.HOURS),
                end = Interval(2, TimeUnit.HOURS),
                RateId(2)
            ),
            SlotBasedTariff.Slot(
                start = Interval(2, TimeUnit.HOURS),
                end = Interval(12, TimeUnit.HOURS),
                RateId(3)
            ),
            SlotBasedTariff.Slot(
                start = Interval(12, TimeUnit.MINUTES),
                end = null,
                RateId(4)
            ),
        ),

        billingInterval = null
    )
    @Test
    fun `test tariff 315 with priceChart`() {
        val gson = Gson()
        val priceChart = gson.fromJson(
            FileReader("src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0315.json"),
            PriceChart::class.java
        )

        priceChart.chart.map { dataPoint ->
            if (dataPoint.end == 0L) return
            val receipt = calculator.calculate(
                tariff = tariff315,
                rentalStart = Instant.ofEpochMilli(TimeUnit.SECONDS.toMillis(0)),
                rentalEnd = Instant.ofEpochMilli(TimeUnit.SECONDS.toMillis(dataPoint.end))
            )

            assertThat(receipt.price, equalTo(dataPoint.price))
        }
    }
}