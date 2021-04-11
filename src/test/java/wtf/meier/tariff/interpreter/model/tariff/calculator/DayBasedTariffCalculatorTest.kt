package wtf.meier.tariff.interpreter.model.tariff.calculator

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.DayBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.util.*
import java.util.concurrent.TimeUnit


internal class DayBasedTariffCalculatorTest {

    private lateinit var calculator: DayBasedTariffCalculator
    private lateinit var calendar: GregorianCalendar

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
        calendar = GregorianCalendar()
    }


    @Test
    fun calculate() {

        calendar.timeZone = tariff.timeZone

        calendar.set(1988, 7, 29, 0, 10)
        val startDate = calendar.time

        calendar.set(1988, 7, 30, 13, 40)
        val startEnd = calendar.time

        val receipt = calculator.calculate(tariff, startDate, startEnd)

        // 2 started days
        print(receipt.price)
        assert(receipt.price == 4000)


    }
}