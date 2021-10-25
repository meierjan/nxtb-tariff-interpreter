package wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.Calculator
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs.testData.PriceChartTester
import java.util.*
import java.util.concurrent.TimeUnit

class tariff92Test {

    private lateinit var rateCalculator: RateCalculator
    private lateinit var calculator: Calculator

    @BeforeEach
    fun setup() {
        rateCalculator = RateCalculator()
        calculator = Calculator()
    }

    private val tariff92 = SlotBasedTariff(
        id = TariffId(1),
        freeSeconds = 0,
        rates = setOf(
            FixedRate(
                id = RateId(1),
                currency = Currency.getInstance("USD"),
                price = Price(250)
            ),
            FixedRate(
                id = RateId(2),
                currency = Currency.getInstance("USD"),
                price = Price(750)
            ),
            TimeBasedRate(
                id = RateId(3),
                currency = Currency.getInstance("USD"),
                interval = Interval(30, TimeUnit.MINUTES),
                basePrice = Price(0),
                maxPrice = Price(Integer.MAX_VALUE),
                minPrice = Price(0),
                pricePerInterval = Price(900),
            ),

            ),
        slots = setOf(
            SlotBasedTariff.Slot(
                start = Interval(0, TimeUnit.MINUTES),
                end = Interval(75, TimeUnit.MINUTES),
                RateId(1)
            ),
            SlotBasedTariff.Slot(
                start = Interval(75, TimeUnit.MINUTES),
                end = Interval(105, TimeUnit.MINUTES),
                RateId(2)
            ),

            SlotBasedTariff.Slot(
                start = Interval(105, TimeUnit.MINUTES),
                end = null,
                RateId(3)
            ),
        ),
        billingInterval = null
    )

    @Test
    // todo priceChart has invalid price!
    fun `test tariff 92 with priceChart`() {
        val tester = PriceChartTester()
        tester.testPriceChart(
            "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0043.json",
            tariff92,
            calculator
        )
    }
}