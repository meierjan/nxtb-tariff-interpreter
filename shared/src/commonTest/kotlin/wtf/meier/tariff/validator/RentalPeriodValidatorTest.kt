package wtf.meier.tariff.validator

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.validator.exception.InvalidRentalPeriodException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.milliseconds

class RentalPeriodValidatorTest {

    private lateinit var invalidRentalPeriod: RentalPeriod

    private val rentalPeriod = RentalPeriod(
        rentalStart = Instant.fromEpochMilliseconds(1),
        rentalEnd = Instant.fromEpochMilliseconds(2)
    )

    @Test
    fun `RentalPeriod - rentalStart after RentalEnd`() {
        invalidRentalPeriod = rentalPeriod.copy(rentalStart = Instant.fromEpochMilliseconds(3))

        assertFailsWith<InvalidRentalPeriodException> { invalidRentalPeriod.accept(Validator) }
    }

    @Test
    fun `RentalPeriod - rental end is after now`() {
        invalidRentalPeriod =
            rentalPeriod.copy(rentalEnd = Clock.System.now().plus(100.milliseconds))

        assertFailsWith<InvalidRentalPeriodException> { invalidRentalPeriod.accept(Validator) }
    }
}