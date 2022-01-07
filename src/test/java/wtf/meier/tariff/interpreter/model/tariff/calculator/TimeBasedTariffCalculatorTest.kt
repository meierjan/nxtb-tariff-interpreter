package wtf.meier.tariff.interpreter.model.tariff.calculator

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class TimeBasedTariffCalculatorTest {

    private lateinit var rateCalculator: RateCalculator
    private lateinit var calculator: TimeBasedTariffCalculator

    private val timezone = TimeZone.getTimeZone(ZoneId.of("GMT"))

    @BeforeEach
    fun setup() {
        rateCalculator = RateCalculator()
        calculator = TimeBasedTariffCalculator(rateCalculator)
    }

    private val rate1 = FixedRate(
        id = RateId(1),
        currency = Currency.getInstance("EUR"),
        price = Price(100)
    )

    private val rate2 = FixedRate(
        id = RateId(2),
        currency = Currency.getInstance("EUR"),
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
        billingInterval = null,
        currency = Currency.getInstance("EUR"),
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
        timeZone = timezone
    )

    @Test
    fun `test Monday 4 PM to Tuesday 9PM`() {

        val start = ZonedDateTime.of(2021, 4, 19, 16, 0, 0, 0, timezone.toZoneId()).toInstant()
        val end = ZonedDateTime.of(2021, 4, 20, 21, 0, 0, 0, timezone.toZoneId()).toInstant()
        val rentalPeriod = RentalPeriod(start, end)

        val receipt = calculator.calculate(tariff1, rentalPeriod)


        assertThat(receipt.price, equalTo(250))

    }

    @Test
    fun `test Tuesday 7AM to Tuesday 8AM`() {

        val start = ZonedDateTime.of(2021, 4, 20, 7, 0, 0, 0, timezone.toZoneId()).toInstant()
        val end = ZonedDateTime.of(2021, 4, 20, 8, 0, 0, 0, timezone.toZoneId()).toInstant()
        val rentalPeriod = RentalPeriod(start, end)

        val receipt = calculator.calculate(tariff1, rentalPeriod)

        // One second into 8AM slot, so additional 100 apply
        assertThat(receipt.price, equalTo(150))

    }


    @Test
    fun `test intersecting slot correct if later slot that day`() {
        // Monday
        val start = ZonedDateTime.of(2021, 4, 19, 16, 0, 0, 0, timezone.toZoneId())

        val slot = calculator.firstIntersectingSlot(tariff1.timeSlots, start)

        assertThat(slot, equalTo(tariff1.timeSlots.first())) // Monday 8am

    }

    @Test
    fun `test intersecting slot correct for same start`() {
        // Sunday
        val start = ZonedDateTime.of(2021, 4, 25, 22, 0, 0, 0, timezone.toZoneId())

        val slot = calculator.firstIntersectingSlot(tariff1.timeSlots, start)

        assertThat(slot, equalTo(tariff1.timeSlots.last())) // Sunday 22pm

    }

    @Test
    fun `test intersecting slot correct if slot started day before`() {
        // Wednesday
        val start = ZonedDateTime.of(2021, 4, 21, 6, 12, 0, 0, timezone.toZoneId())

        val slot = calculator.firstIntersectingSlot(tariff1.timeSlots, start)

        assertThat(slot, equalTo(tariff1.timeSlots[3])) // TUESDAY 22pm

    }


}