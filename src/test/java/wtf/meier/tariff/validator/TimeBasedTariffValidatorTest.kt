package wtf.meier.tariff.validator

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff
import wtf.meier.tariff.validator.exception.tariff.*
import java.time.DayOfWeek
import java.util.*

class TimeBasedTariffValidatorTest {

    private val basicFixedRate = FixedRate(
        id = RateId(1),
        currency = Currency.getInstance("EUR"),
        price = Price(3)
    )

    private val firstTime = TimeBasedTariff.TimeSlot.Time(
        day = DayOfWeek.FRIDAY,
        hour = 5,
        minutes = 0
    )

    private val secondTime = TimeBasedTariff.TimeSlot.Time(
        day = DayOfWeek.MONDAY,
        hour = 5,
        minutes = 0
    )

    private val thirdTime = TimeBasedTariff.TimeSlot.Time(
        day = DayOfWeek.THURSDAY,
        hour = 5,
        minutes = 0
    )

    private
    val timeBasedTariff = TimeBasedTariff(
        id = TariffId(1),
        billingInterval = null,
        goodwill = null,
        currency = Currency.getInstance("EUR"),
        timeZone = TimeZone.getTimeZone("GMT"),
        rates = setOf(basicFixedRate),
        timeSlots = listOf(
            TimeBasedTariff.TimeSlot(
                from = firstTime,
                to = firstTime,
                rate = RateId(1)
            )

        )
    )

    @Test
    fun `TimeBasedTariff - inconsistent currency`() {
        val invalidTariff = timeBasedTariff.copy(
            currency = Currency.getInstance("USD")
        )
        assertThrows<InconsistentCurrencyException> { invalidTariff.accept(Validator) }
    }

    @Test
    fun `SlotBasedTariff - two rates with same rateId`() {
        val invalidTariff = timeBasedTariff.copy(
            rates = setOf(
                basicFixedRate,
                basicFixedRate.copy(price = Price(300))
            )
        )
        assertThrows<InvalidRateIdException> { invalidTariff.accept(Validator) }
    }

    @Test
    fun `SlotBasedTariff - two overlapping TimeSlots`() {
        val invalidTariff = timeBasedTariff.copy(
            timeSlots = listOf(
                TimeBasedTariff.TimeSlot(
                    from = firstTime,
                    to = thirdTime,
                    rate = RateId(1)
                ),
                TimeBasedTariff.TimeSlot(
                    from = secondTime,
                    to = firstTime,
                    rate = RateId(1)
                )
            )
        )
        val exception = assertThrows<InvalidTimeSlotException> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("the timeSlot with start value: ") ?: false)
    }

    @Test
    fun `SlotBasedTariff - two unconnected TimeSlots`() {
        val invalidTariff = timeBasedTariff.copy(
            timeSlots = listOf(
                TimeBasedTariff.TimeSlot(
                    from = firstTime,
                    to = secondTime,
                    rate = RateId(1)
                ),
                TimeBasedTariff.TimeSlot(
                    from = thirdTime,
                    to = firstTime,
                    rate = RateId(1)
                )
            )
        )
        val exception = assertThrows<InvalidTimeSlotException> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("the timeSlot with start value: ") ?: false)
    }

    @Test
    fun `SlotBasedTariff - last slot ends not with start time`() {
        val invalidTariff = timeBasedTariff.copy(
            timeSlots = listOf(
                TimeBasedTariff.TimeSlot(
                    from = firstTime,
                    to = secondTime,
                    rate = RateId(1)
                )
            )
        )
        val exception = assertThrows<InvalidTimeSlotException> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("slots covers not exactly a week") ?: false)
    }

    @Test
    fun `SlotBasedTariff - slots covers more then one week`() {
        val invalidTariff = timeBasedTariff.copy(
            timeSlots = listOf(
                TimeBasedTariff.TimeSlot(
                    from = firstTime,
                    to = firstTime,
                    rate = RateId(1)
                ),
                TimeBasedTariff.TimeSlot(
                    from = firstTime,
                    to = firstTime,
                    rate = RateId(1)
                )
            )
        )
        print(DayOfWeek.FRIDAY.value)
        val exception = assertThrows<InvalidTimeSlotException> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("slots covers not exactly a week") ?: false)

    }

    @Test
    fun `SlotBasedTariff - slots covers more then one week with three slots`() {
        val invalidTariff = timeBasedTariff.copy(
            timeSlots = listOf(
                TimeBasedTariff.TimeSlot(
                    from = firstTime,
                    to = thirdTime,
                    rate = RateId(1)
                ),
                TimeBasedTariff.TimeSlot(
                    from = thirdTime,
                    to = secondTime,
                    rate = RateId(1)
                ),
                TimeBasedTariff.TimeSlot(
                    from = secondTime,
                    to = firstTime,
                    rate = RateId(1)
                )
            )
        )
        print(DayOfWeek.FRIDAY.value)
        val exception = assertThrows<InvalidTimeSlotException> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("slots covers not exactly a week") ?: false)

    }

    @Test
    fun `SlotBasedTariff - slots covers one week`() {
        val invalidTariff = timeBasedTariff.copy(
            timeSlots = listOf(
                TimeBasedTariff.TimeSlot(
                    from = firstTime,
                    to = firstTime.copy(hour = 6),
                    rate = RateId(1)
                ),
                TimeBasedTariff.TimeSlot(
                    from = firstTime.copy(hour = 6),
                    to = firstTime,
                    rate = RateId(1)
                )
            )
        )
        invalidTariff.accept(Validator)
    }

    @Test
    fun `SlotBasedTariff - slots is empty`() {
        val invalidTariff = timeBasedTariff.copy(
            timeSlots = listOf()
        )
        val exception = assertThrows<InvalidTimeBasedTariffException> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("timeSlots is empty") ?: false)
    }
}