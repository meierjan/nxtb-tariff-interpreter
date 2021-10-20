package wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs

import com.google.gson.Gson
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
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

class tariff299Test {

    private lateinit var rateCalculator: RateCalculator
    private lateinit var calculator: SlotBasedTariffCalculator

    @BeforeEach
    fun setup() {
        rateCalculator = RateCalculator()
        calculator = SlotBasedTariffCalculator()
    }

    private val tariff299 = SlotBasedTariff(
        id = TariffId(1),
        freeSeconds = TimeUnit.MINUTES.toSeconds(30).toInt(),
        rates = setOf(
            TimeBasedRate(
                id = RateId(1),
                currency = Currency.getInstance("CZK"),
                interval = Interval(30, TimeUnit.MINUTES),
                basePrice = Price(0),
                maxPrice = Price(15000),
                minPrice = Price(0),
                pricePerInterval = Price(2400),
            ),
        ),
        slots = setOf(
            SlotBasedTariff.Slot(
                start = Interval(0, TimeUnit.MINUTES),
                end = null,
                RateId(1)
            ),
        ),
        billingInterval = Interval(1, TimeUnit.DAYS)
    )


    @Test
    fun `test tariff 299 with priceChart`() {
        val gson = Gson()
        val priceChart = gson.fromJson(
            FileReader("src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0299.json"),
            PriceChart::class.java
        )

        priceChart.chart.map { dataPoint ->
            if (dataPoint.end == 0L) return
            val receipt = calculator.calculate(
                tariff = tariff299,
                rentalStart = Instant.ofEpochMilli(TimeUnit.SECONDS.toMillis(0)),
                rentalEnd = Instant.ofEpochMilli(TimeUnit.SECONDS.toMillis(dataPoint.end))
            )

            assertThat(receipt.price, equalTo(dataPoint.price))
        }
    }
}