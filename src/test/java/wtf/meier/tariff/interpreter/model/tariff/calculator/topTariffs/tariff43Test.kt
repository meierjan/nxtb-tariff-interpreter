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


class tariff43Test {

    private lateinit var rateCalculator: RateCalculator
    private lateinit var calculator: Calculator

    @BeforeEach
    fun setup() {
        rateCalculator = RateCalculator()
        calculator = Calculator()
    }

    private val tariff43 = SlotBasedTariff(
        id = TariffId(1),
        freeSeconds = TimeUnit.MINUTES.toSeconds(20).toInt(),
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
            FixedRate(
                id = RateId(3),
                currency = Currency.getInstance("PLN"),
                price = Price(500)
            ),
            TimeBasedRate(
                id = RateId(4),
                currency = Currency.getInstance("PLN"),
                interval = Interval(1, TimeUnit.HOURS),
                basePrice = Price(0),
                maxPrice = Price(Int.MAX_VALUE),
                minPrice = Price(0),
                pricePerInterval = Price(700),
            ),
            FixedRate(
                id = RateId(5),
                currency = Currency.getInstance("PLN"),
                price = Price(20000)
            )
        ),
        slots = setOf(
            SlotBasedTariff.Slot(
                start = Interval(0, TimeUnit.MINUTES),
                end = Interval(60, TimeUnit.MINUTES),
                RateId(1)
            ),
            SlotBasedTariff.Slot(
                start = Interval(60, TimeUnit.MINUTES),
                end = Interval(120, TimeUnit.MINUTES),
                RateId(2)
            ),
            SlotBasedTariff.Slot(
                start = Interval(120, TimeUnit.MINUTES),
                end = Interval(180, TimeUnit.MINUTES),
                RateId(3)
            ),
            SlotBasedTariff.Slot(
                start = Interval(180, TimeUnit.MINUTES),
                end = Interval(12, TimeUnit.HOURS),
                RateId(4)
            ),
            SlotBasedTariff.Slot(
                start = Interval(12, TimeUnit.HOURS),
                end = null,
                RateId(5)
            )
        ),
        billingInterval = null
    )


    @Test
    fun `test tariff 43`() {
        val tester = PriceChartTester()
        tester.testPriceChart(
            "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0043.json",
            tariff43,
            calculator
        )
    }
}
