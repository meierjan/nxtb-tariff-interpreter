package wtf.meier.tariff.interpreter.model.tariff.calculator

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.DayBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit


internal class DayBasedTariffCalculatorTest {

    private lateinit var calculator: DayBasedTariffCalculator

    private val tariff = DayBasedTariff(
        id = TariffId(1),
        freeSeconds = 0,
        rates = mutableSetOf(
            FixedRate(
                id = RateId(1),
                currency = Currency.getInstance("EUR"),
                price = Price(2000)
            )
        ),
        // TODO: maybe make nullable for infinite
        billingInterval = Interval(100, TimeUnit.DAYS),
        TimeZone.getTimeZone("GMT+1")
    )


    @BeforeEach
    fun setup() {
        calculator = DayBasedTariffCalculator()
    }


    @Test
    fun calculate() {

        val startInstant = LocalDateTime.of(1988, 7, 29, 0, 10)
            .atZone(tariff.timeZone.toZoneId())
            .toInstant()

        val startEnd = LocalDateTime.of(1988, 7, 30, 13, 40)
            .atZone(tariff.timeZone.toZoneId())
            .toInstant()

        val receipt = calculator.calculate(tariff, startInstant, startEnd)

        // 2 started days

        assert(receipt.price == 4000)


    }
}