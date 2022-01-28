package wtf.meier.tariff.validator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.validator.exception.tariff.InvalidIntervalException
import java.util.concurrent.TimeUnit

class IntervalValidatorTest {
    @Test
    fun `Interval - negative timeAmount`() {
        val interval = Interval(-2, TimeUnit.MINUTES)
        assertThrows<InvalidIntervalException> { interval.accept(Validator) }
    }
}