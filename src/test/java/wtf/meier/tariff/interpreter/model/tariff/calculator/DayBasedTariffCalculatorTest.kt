package wtf.meier.tariff.interpreter.model.tariff.calculator

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import wtf.meier.tariff.interpreter.model.tariff.DayBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit


internal class DayBasedTariffCalculatorTest {

    private lateinit var calculator: DayBasedTariffCalculator

    private val firstSlot = DayBasedTariff.RentalSynchronizedSlot(
        rate = RateId(1),
        start = Interval(0, TimeUnit.DAYS),
        end = Interval(2, TimeUnit.HOURS)
    )
    private val secondSlot = DayBasedTariff.RentalSynchronizedSlot(
        rate = RateId(2),
        start = Interval(2, TimeUnit.HOURS),
        end = Interval(3, TimeUnit.HOURS)
    )
    private val thirdSlot = DayBasedTariff.DaySynchronisedSlot(
        rate = RateId(3),
        startDay = 1,
        endDay = 3
    )
    private val forthSlot = DayBasedTariff.DaySynchronisedSlot(
        rate = RateId(4),
        startDay = 3,
        endDay = 6
    )
    private val fifthSlot = DayBasedTariff.DaySynchronisedSlot(
        rate = RateId(5),
        startDay = 6,
    )


    private val firstRate = TimeBasedRate(
        id = RateId(1),
        currency = Currency.getInstance("EUR"),
        interval = Interval(1, TimeUnit.HOURS),
        basePrice = Price(0),
        pricePerInterval = Price(300),
        maxPrice = Price(Int.MAX_VALUE),
        minPrice = Price(0)
    )
    private val secondRate = FixedRate(
        id = RateId(2),
        currency = Currency.getInstance("EUR"),
        price = Price(0)
    )
    private val thirdRate = FixedRate(
        id = RateId(3),
        currency = Currency.getInstance("EUR"),
        price = Price(900),
    )
    private val forthRate = FixedRate(
        id = RateId(4),
        currency = Currency.getInstance("EUR"),
        price = Price(800),
    )
    private val fifthRate = TimeBasedRate(
        id = RateId(5),
        currency = Currency.getInstance("EUR"),
        interval = Interval(1, TimeUnit.DAYS),
        basePrice = Price(0),
        pricePerInterval = Price(750),
        maxPrice = Price(Int.MAX_VALUE),
        minPrice = Price(0)
    )

    private val usedom38Tariff = DayBasedTariff(
        id = TariffId(1),
        rates = setOf(
            firstRate,
            secondRate,
            thirdRate,
            forthRate,
            fifthRate
        ),
        currency = Currency.getInstance("EUR"),
        timeZone = TimeZone.getTimeZone("GMT+1"),
        slots = setOf(
            firstSlot,
            secondSlot,
            thirdSlot,
            forthSlot,
            fifthSlot
        ),
        billingInterval = null
    )

    @Test
    fun findBestSlot() {
        val duration = Instant.ofEpochSecond(TimeUnit.HOURS.toSeconds(9))
        val slots = setOf<DayBasedTariff.Slot>(
            firstSlot,
            secondSlot,
            thirdSlot,
            forthSlot
        )
        val slot = DayBasedTariffCalculator().findMatchingSlot(slots, duration, 4)
        assertThat(slot, equalTo(forthSlot))
    }

    @Test
    fun `test DayBasedCalculator RentalSynchronizedSlots 2h`() {

        val startInstant = LocalDateTime.of(1988, 7, 29, 0, 10)
            .atZone(usedom38Tariff.timeZone.toZoneId())
            .toInstant()

        val startEnd = LocalDateTime.of(1988, 7, 29, 2, 10)
            .atZone(usedom38Tariff.timeZone.toZoneId())
            .toInstant()

        val rentalPeriod = RentalPeriod(startInstant, startEnd)
        calculator = DayBasedTariffCalculator()
        val receipt = calculator.calculate(usedom38Tariff, rentalPeriod)

        assertThat(receipt.price, equalTo(600))
    }


    @Test
    fun `test DayBasedCalculator DaySynchronizedSlots 9h`() {

        val startInstant = LocalDateTime.of(1988, 7, 29, 0, 10)
            .atZone(usedom38Tariff.timeZone.toZoneId())
            .toInstant()

        val startEnd = LocalDateTime.of(1988, 7, 29, 9, 10)
            .atZone(usedom38Tariff.timeZone.toZoneId())
            .toInstant()

        val rentalPeriod = RentalPeriod(startInstant, startEnd)
        calculator = DayBasedTariffCalculator()
        val receipt = calculator.calculate(usedom38Tariff, rentalPeriod)

        assertThat(receipt.price, equalTo(900))
    }

    @Test
    fun `test DayBasedCalculator DaySynchronizedSlots 2 days`() {

        val startInstant = LocalDateTime.of(1988, 7, 20, 0, 10)
            .atZone(usedom38Tariff.timeZone.toZoneId())
            .toInstant()

        val startEnd = LocalDateTime.of(1988, 7, 21, 0, 10)
            .atZone(usedom38Tariff.timeZone.toZoneId())
            .toInstant()

        val rentalPeriod = RentalPeriod(startInstant, startEnd)
        calculator = DayBasedTariffCalculator()
        val receipt = calculator.calculate(usedom38Tariff, rentalPeriod)

        assertThat(receipt.price, equalTo(1800))
    }

    @Test
    fun `test DayBasedCalculator DaySynchronizedSlots 3 days`() {

        val startInstant = LocalDateTime.of(1988, 7, 20, 0, 10)
            .atZone(usedom38Tariff.timeZone.toZoneId())
            .toInstant()

        val startEnd = LocalDateTime.of(1988, 7, 22, 0, 10)
            .atZone(usedom38Tariff.timeZone.toZoneId())
            .toInstant()

        val rentalPeriod = RentalPeriod(startInstant, startEnd)
        calculator = DayBasedTariffCalculator()
        val receipt = calculator.calculate(usedom38Tariff, rentalPeriod)

        assertThat(receipt.price, equalTo(2400))
    }

    @Test
    fun `test DayBasedCalculator DaySynchronizedSlots 10 days`() {

        val startInstant = LocalDateTime.of(1988, 7, 20, 0, 10)
            .atZone(usedom38Tariff.timeZone.toZoneId())
            .toInstant()

        val startEnd = LocalDateTime.of(1988, 7, 29, 0, 10)
            .atZone(usedom38Tariff.timeZone.toZoneId())
            .toInstant()

        val rentalPeriod = RentalPeriod(startInstant, startEnd)
        calculator = DayBasedTariffCalculator()
        val receipt = calculator.calculate(usedom38Tariff, rentalPeriod)
        println(Json.encodeToString(usedom38Tariff))
        assertThat(receipt.price, equalTo(7500))
    }


}