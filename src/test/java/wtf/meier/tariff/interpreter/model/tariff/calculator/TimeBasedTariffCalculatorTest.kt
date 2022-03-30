package wtf.meier.tariff.interpreter.model.tariff.calculator

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.goodwill.FreeMinutes
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import wtf.meier.tariff.interpreter.model.billingInterval.BillingInterval
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class TimeBasedTariffCalculatorTest {

    private lateinit var rateCalculator: RateCalculator
    private lateinit var calculator: TimeBasedTariffCalculator

    private val timeZone = TimeZone.getTimeZone(ZoneId.of("GMT"))
    private val currency = Currency.getInstance("EUR")

    @BeforeEach
    fun setup() {
        rateCalculator = RateCalculator()
        calculator = TimeBasedTariffCalculator(rateCalculator)
    }

    private val rate1 = FixedRate(
        id = RateId(1),
        currency = currency,
        price = Price(100)
    )

    private val rate2 = FixedRate(
        id = RateId(2),
        currency = currency,
        price = Price(50)
    )

    /**
     * tariff:
     *  every day
     *  08:00 AM -> 10:00 PM -> 1 Euro
     *  10:00 PM -> 08:00 AM -> 0.5 Euro
     */
    private val tariff1 = TimeBasedTariff(
        id = TariffId(1),
        currency = currency,
        rates = setOf(
            rate1,
            rate2
        ),
        timeSlots = listOf(
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),


            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.FRIDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.FRIDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.FRIDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.FRIDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SATURDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SATURDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SATURDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SATURDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SUNDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SUNDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SUNDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SUNDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),
        ),
        timeZone = timeZone
    )

    @Test
    fun `test Monday 4 PM to Tuesday 9PM`() {

        val start = ZonedDateTime.of(2021, 4, 19, 16, 0, 0, 0, timeZone.toZoneId()).toInstant()
        val end = ZonedDateTime.of(2021, 4, 20, 21, 0, 0, 0, timeZone.toZoneId()).toInstant()
        val rentalPeriod = RentalPeriod(start, end)

        val receipt = calculator.calculate(tariff1, rentalPeriod)


        assertThat(receipt.price, equalTo(250))

    }

    @Test
    fun `test Tuesday 7AM to Tuesday 8AM`() {

        val start = ZonedDateTime.of(2021, 4, 20, 7, 0, 0, 0, timeZone.toZoneId()).toInstant()
        val end = ZonedDateTime.of(2021, 4, 20, 8, 0, 0, 0, timeZone.toZoneId()).toInstant()
        val rentalPeriod = RentalPeriod(start, end)

        val receipt = calculator.calculate(tariff1, rentalPeriod)

        // One second into 8AM slot, so additional 100 apply
        assertThat(receipt.price, equalTo(150))

    }


    @Test
    fun `test intersecting slot correct if later slot that day`() {
        // Monday
        val start = ZonedDateTime.of(2021, 4, 19, 16, 0, 0, 0, timeZone.toZoneId())

        val slot = calculator.firstIntersectingSlot(tariff1.timeSlots, start)

        assertThat(slot, equalTo(tariff1.timeSlots.first())) // Monday 8am

    }

    @Test
    fun `test intersecting slot correct for same start`() {
        // Sunday
        val start = ZonedDateTime.of(2021, 4, 25, 22, 0, 0, 0, timeZone.toZoneId())

        val slot = calculator.firstIntersectingSlot(tariff1.timeSlots, start)

        assertThat(slot, equalTo(tariff1.timeSlots.last())) // Sunday 22pm

    }

    @Test
    fun `test intersecting slot correct if slot started day before`() {
        // Wednesday
        val start = ZonedDateTime.of(2021, 4, 21, 6, 12, 0, 0, timeZone.toZoneId())

        val slot = calculator.firstIntersectingSlot(tariff1.timeSlots, start)

        assertThat(slot, equalTo(tariff1.timeSlots[3])) // TUESDAY 22pm

    }

    private var tariff2 = TimeBasedTariff(
        id = TariffId(2),
        currency = currency,
        timeZone = timeZone,
        timeSlots = listOf(
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    day = DayOfWeek.FRIDAY,
                    hour = 16,
                    minutes = 0,
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    day = DayOfWeek.MONDAY,
                    hour = 6,
                    minutes = 0
                ),
                RateId(1)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    day = DayOfWeek.MONDAY,
                    hour = 6,
                    minutes = 0,
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    day = DayOfWeek.FRIDAY,
                    hour = 16,
                    minutes = 0
                ),
                RateId(2)
            )
        ),
        rates = setOf(
            FixedRate(
                id = RateId(1),
                price = Price(10),
                currency = currency
            ),
            TimeBasedRate(
                id = RateId(2),
                currency = currency,
                interval = Interval(1, TimeUnit.DAYS),
                pricePerInterval = Price(5),
                minPrice = Price(0),
                maxPrice = Price(Integer.MAX_VALUE),
                basePrice = Price(0)
            ),
        ),
    )

    @Test
    fun `test rental duration of 0`() {
        val start = ZonedDateTime.of(2022, 1, 1, 3, 0, 0, 0, timeZone.toZoneId()).toInstant()
        val rentalPeriod = RentalPeriod(start, start)

        val receipt = calculator.calculate(tariff2, rentalPeriod)
        assertThat(receipt.price, equalTo(0))
    }

    @Test
    fun `test calculation only first interval of fixedRate`() {
        // it's a Monday
        val start = ZonedDateTime.of(2022, 1, 3, 15, 0, 0, 0, timeZone.toZoneId()).toInstant()
        val end = ZonedDateTime.of(2022, 1, 3, 15, 10, 0, 0, timeZone.toZoneId()).toInstant()

        val rentalPeriod = RentalPeriod(start, end)

        val receipt = calculator.calculate(tariff2, rentalPeriod)
        assertThat(receipt.price, equalTo(5))
    }


    private val rate0268 = TimeBasedTariff(
        id = TariffId(1),
        billingInterval = BillingInterval(Interval(1, TimeUnit.DAYS), maxPrice = Price(1200)),
        goodwill = null,
        currency = currency,
        timeZone = timeZone,
        rates = setOf(
            TimeBasedRate(
                id = RateId(1),
                currency = currency,
                interval = Interval(30, TimeUnit.MINUTES),
                basePrice = Price(0),
                minPrice = Price(0),
                maxPrice = Price(Integer.MAX_VALUE),
                pricePerInterval = Price(100)
            ),
            FixedRate(
                id = RateId(2),
                currency = currency,
                price = Price(200)
            )
        ),
        timeSlots = listOf(
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 17,
                    minutes = 0
                ),
                rate = RateId(1)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 17,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = RateId(2)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 17,
                    minutes = 0
                ),
                rate = RateId(1)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 17,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = RateId(2)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 17,
                    minutes = 0
                ),
                rate = RateId(1)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 17,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = RateId(2)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 17,
                    minutes = 0
                ),
                rate = RateId(1)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 17,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.FRIDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = RateId(2)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.FRIDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.FRIDAY,
                    hour = 17,
                    minutes = 0
                ),
                rate = RateId(1)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.FRIDAY,
                    hour = 17,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SATURDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = RateId(2)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SATURDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SATURDAY,
                    hour = 17,
                    minutes = 0
                ),
                rate = RateId(1)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SATURDAY,
                    hour = 17,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SUNDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = RateId(2)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SUNDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SUNDAY,
                    hour = 17,
                    minutes = 0
                ),
                rate = RateId(1)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SUNDAY,
                    hour = 17,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = RateId(2)
            ),
        )
    )

    @Test
    fun `test Rate0268 - 8_30-16_30`() {
        val start = ZonedDateTime.of(2022, 1, 1, 8, 30, 0, 0, timeZone.toZoneId()).toInstant()
        val end = ZonedDateTime.of(2022, 1, 1, 16, 30, 0, 0, timeZone.toZoneId()).toInstant()

        val receipt = calculator.calculate(rate0268, RentalPeriod(start, end))
        assertThat(receipt.price, equalTo(1200))
    }

    @Test
    fun `test Rate0268 - 16_00-13_00`() {
        val start = ZonedDateTime.of(2022, 1, 1, 16, 0, 0, 0, timeZone.toZoneId()).toInstant()
        val end = ZonedDateTime.of(2022, 1, 2, 13, 0, 0, 0, timeZone.toZoneId()).toInstant()

        val receipt = calculator.calculate(rate0268, RentalPeriod(start, end))
        assertThat(receipt.price, equalTo(1200))
    }

    @Test
    fun `test Rate0268 - 16_00-16_00 - two days`() {
        val start = ZonedDateTime.of(2022, 1, 1, 16, 0, 0, 0, timeZone.toZoneId()).toInstant()
        val end = ZonedDateTime.of(2022, 1, 3, 16, 0, 0, 0, timeZone.toZoneId()).toInstant()

        val receipt = calculator.calculate(rate0268, RentalPeriod(start, end))
        assertThat(receipt.price, equalTo(2400))
        assertThat(receipt.positions.size, equalTo(1))
    }


    private val tariff342 = TimeBasedTariff(
        id = TariffId(342),
        currency = currency,
        billingInterval = BillingInterval(duration = Interval(1, TimeUnit.DAYS), maxPrice = Price(1000)),
        timeZone = timeZone,
        goodwill = FreeMinutes(Interval(60, TimeUnit.MINUTES)),
        rates = setOf(
            FixedRate(
                id = RateId(1),
                currency = currency,
                price = Price(300)
            ),
            TimeBasedRate(
                id = RateId(2),
                currency = currency,
                interval = Interval(30, TimeUnit.MINUTES),
                pricePerInterval = Price(100),
                minPrice = Price(0),
                maxPrice = Price(Int.MAX_VALUE),
                basePrice = Price(0)
            )
        ),
        timeSlots = listOf(
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 0,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 9,
                    minutes = 0
                ),
                rate = RateId(1)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 9,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 18,
                    minutes = 0
                ),
                rate = RateId(2)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 18,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 9,
                    minutes = 0
                ),
                rate = RateId(1)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 9,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 18,
                    minutes = 0
                ),
                rate = RateId(2)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 18,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 9,
                    minutes = 0
                ),
                rate = RateId(1)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 9,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 18,
                    minutes = 0
                ),
                rate = RateId(2)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 18,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 9,
                    minutes = 0
                ),
                rate = RateId(1)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 9,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 18,
                    minutes = 0
                ),
                rate = RateId(2)
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 18,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.FRIDAY,
                    hour = 9,
                    minutes = 0
                ),
                rate = RateId(1)
            ),
        )
    )


    //TODO is it right??
    @Test
    fun `test rate 342 - Monday 7am to Tuesday 8am`() {
        val start = ZonedDateTime.of(2022, 1, 3, 7, 0, 0, 0, timeZone.toZoneId()).toInstant()
        val end = ZonedDateTime.of(2022, 1, 4, 8, 0, 0, 0, timeZone.toZoneId()).toInstant()

        val receipt = calculator.calculate(tariff342, RentalPeriod(start, end))
        assertThat(receipt.price, equalTo(1300))
    }

}
