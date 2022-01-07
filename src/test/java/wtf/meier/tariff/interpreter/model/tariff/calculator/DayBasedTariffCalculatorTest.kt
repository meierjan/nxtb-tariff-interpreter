package wtf.meier.tariff.interpreter.model.tariff.calculator

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
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
        currency = Currency.getInstance("EUR"),
        rates = mutableSetOf(
            FixedRate(
                id = RateId(1),
                currency = Currency.getInstance("EUR"),
                price = Price(2000)
            )
        ),
        // TODO: maybe make nullable for infinite
        billingInterval = Interval(100, TimeUnit.DAYS),
        timeZone = TimeZone.getTimeZone("GMT+1")
    )


    @BeforeEach
    fun setup() {
        calculator = DayBasedTariffCalculator()
    }

    private val usedomTariff = DayBasedTariff(
        id = TariffId(1),
        currency = Currency.getInstance("EUR"),
        rates = mutableSetOf(
            FixedRate(
                id = RateId(1),
                currency = Currency.getInstance("EUR"),
                price = Price(1500)
            )
        ),
        billingInterval = (Interval(100, TimeUnit.DAYS)),
        timeZone = TimeZone.getTimeZone("GMT+1")
    )

    // TODO dayBasedTariff revise
    @Test
    fun usedom() {
        val startInstant = LocalDateTime.of(1988, 7, 29, 0, 10)
            .atZone(tariff.timeZone.toZoneId())
            .toInstant()

        val startEnd = LocalDateTime.of(1988, 7, 30, 13, 40)
            .atZone(tariff.timeZone.toZoneId())
            .toInstant()

        val rentalPeriod = RentalPeriod(startInstant, startEnd)

        val receipt = calculator.calculate(usedomTariff, rentalPeriod)

        // 2 started days

        assertThat(receipt.price, equalTo(3000))
    }


    @Test
    fun calculate() {

        val startInstant = LocalDateTime.of(1988, 7, 29, 0, 10)
            .atZone(tariff.timeZone.toZoneId())
            .toInstant()

        val startEnd = LocalDateTime.of(1988, 7, 30, 13, 40)
            .atZone(tariff.timeZone.toZoneId())
            .toInstant()

        val rentalPeriod = RentalPeriod(startInstant, startEnd)

        val receipt = calculator.calculate(tariff, rentalPeriod)

        // 2 started days

        assertThat(receipt.price, equalTo(4000))


    }
}