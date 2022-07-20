package wtf.meier.tariff.validator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff
import wtf.meier.tariff.validator.exception.tariff.InvalidTimeException
import java.time.DayOfWeek

class TimeValidatorException {

    private var time = TimeBasedTariff.TimeSlot.Time(
        day = DayOfWeek.FRIDAY,
        hour = 0,
        minutes = 0
    )

    @Test
    fun `Time - negative hours`() {
        time = time.copy(hour = -1)
        assertThrows<InvalidTimeException> { time.accept(Validator) }
    }

    @Test
    fun `Time - over 23 hours`() {
        time = time.copy(hour = 24)
        assertThrows<InvalidTimeException> { time.accept(Validator) }
    }

    @Test
    fun `Time - negative minutes`() {
        time = time.copy(minutes = -1)
        print(time)
        assertThrows<InvalidTimeException> { time.accept(Validator) }
    }

    @Test
    fun `Time - over 59 minutes`() {
        time = time.copy(minutes = 60)
        print(time)
        assertThrows<InvalidTimeException> { time.accept(Validator) }
    }

}