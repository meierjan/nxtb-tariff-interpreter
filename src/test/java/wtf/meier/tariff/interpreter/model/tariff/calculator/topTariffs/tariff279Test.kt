package wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs

import com.google.gson.Gson
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.Calculator
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.tariff.interpreter.model.tariff.calculator.SlotBasedTariffCalculator
import wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs.testData.PriceChart
import wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs.testData.PriceChartTester
import java.io.FileReader
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

class tariff279Test {

    private lateinit var rateCalculator: RateCalculator
    private lateinit var calculator: Calculator

    @BeforeEach
    fun setup() {
        rateCalculator = RateCalculator()
        calculator = Calculator()
    }

    private val tariff279 = SlotBasedTariff(
        id = TariffId(1),
        freeSeconds = 60,
        rates = setOf(
            TimeBasedRate(
                id = RateId(1),
                currency = Currency.getInstance("EUR"),
                interval = Interval(1, TimeUnit.MINUTES),
                basePrice = Price(0),
                maxPrice = Price(1000),
                minPrice = Price(0),
                pricePerInterval = Price(5),
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
    fun `test tariff 279 with priceChart`() {
        val tester = PriceChartTester()
        tester.testPriceChart(
            "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0279.json",
            tariff279,
            calculator
        )

    }
}