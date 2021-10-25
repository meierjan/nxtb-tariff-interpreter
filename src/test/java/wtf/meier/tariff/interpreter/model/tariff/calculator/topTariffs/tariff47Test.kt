package wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs

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
import wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs.testData.PriceChartTester
import java.util.*
import java.util.concurrent.TimeUnit

class tariff47Test {

    private lateinit var rateCalculator: RateCalculator
    private lateinit var calculator: Calculator

    @BeforeEach
    fun setup() {
        rateCalculator = RateCalculator()
        calculator = Calculator()
    }

    private val tariff47 = SlotBasedTariff(
        id = TariffId(1),
        freeSeconds = TimeUnit.MINUTES.toSeconds(35).toInt(),
        rates = setOf(
            TimeBasedRate(
                id = RateId(1),
                currency = Currency.getInstance("EUR"),
                interval = Interval(30, TimeUnit.MINUTES),
                basePrice = Price(0),
                maxPrice = Price(500),
                minPrice = Price(0),
                pricePerInterval = Price(50),
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
    // todo fair tariffs are unsupported...
    fun `test tariff 47 with priceChart`() {

        val tester = PriceChartTester()
        tester.testPriceChart(
            "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0047.json",
            tariff47,
            calculator
        )
    }
}