package wtf.meier.tariff.validator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.validator.exception.InvalidRentalPeriodException
import java.time.Instant

class RentalPeriodValidatorTest {

    private lateinit var invalidRentalPeriod: RentalPeriod

    private val rentalPeriod = RentalPeriod(
        rentalStart = Instant.ofEpochMilli(1),
        rentalEnd = Instant.ofEpochMilli(2)
    )

    @Test
    fun `RentalPeriod - rentalStart after RentalEnd`() {
        invalidRentalPeriod = rentalPeriod.copy(rentalStart = Instant.ofEpochMilli(3))

        assertThrows<InvalidRentalPeriodException> { invalidRentalPeriod.accept(Validator) }
    }

    @Test
    fun `RentalPeriod - rental end is after now`() {
        invalidRentalPeriod = rentalPeriod.copy(rentalEnd = Instant.now().plusMillis(100))

        assertThrows<InvalidRentalPeriodException> { invalidRentalPeriod.accept(Validator) }
    }
}