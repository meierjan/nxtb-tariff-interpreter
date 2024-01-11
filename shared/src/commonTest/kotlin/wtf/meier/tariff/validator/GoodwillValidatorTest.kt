package wtf.meier.tariff.validator

import wtf.meier.tariff.interpreter.model.goodwill.DynamicGoodwill
import wtf.meier.tariff.interpreter.model.goodwill.FreeMinutes
import wtf.meier.tariff.interpreter.model.goodwill.Goodwill
import wtf.meier.tariff.interpreter.model.goodwill.StaticGoodwill
import wtf.meier.tariff.validator.exception.tariff.InvalidDynamicGoodwillException
import wtf.meier.tariff.validator.exception.tariff.InvalidIntervalException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.minutes

class GoodwillValidatorTest {

    lateinit var invalidGoodwill: Goodwill

    @Test
    fun `DynamicGoodwill - negative percentages`() {
        invalidGoodwill = DynamicGoodwill(
            deductibleProportionInPercentage = -Double.MIN_VALUE
        )
        assertFailsWith<InvalidDynamicGoodwillException> { invalidGoodwill.accept(Validator) }

        invalidGoodwill = DynamicGoodwill(
            deductibleProportionInPercentage = -Double.MAX_VALUE
        )
        assertFailsWith<InvalidDynamicGoodwillException> { invalidGoodwill.accept(Validator) }
    }

    @Test
    fun `DynamicGoodwill - over 100 percent`() {
        invalidGoodwill = DynamicGoodwill(
            deductibleProportionInPercentage = 100.1
        )
        assertFailsWith<InvalidDynamicGoodwillException> { invalidGoodwill.accept(Validator) }

        invalidGoodwill = DynamicGoodwill(
            deductibleProportionInPercentage = Double.MAX_VALUE
        )
        assertFailsWith<InvalidDynamicGoodwillException> { invalidGoodwill.accept(Validator) }
    }

    @Test
    fun `DynamicGoodwill - valid percentages`() {
        for (i in 0..100) {
            invalidGoodwill = DynamicGoodwill(
                deductibleProportionInPercentage = i.toDouble()
            )
            invalidGoodwill.accept(Validator)
        }
    }

    @Test
    fun `StaticGoodwill - negative Interval`() {
        invalidGoodwill = StaticGoodwill(
            duration = -100.minutes
        )
        assertFailsWith<InvalidIntervalException> { invalidGoodwill.accept(Validator) }
    }

    @Test
    fun `FreeMinutes - negative Interval`() {
        invalidGoodwill = FreeMinutes(
            duration = -100.minutes
        )
        assertFailsWith<InvalidIntervalException> { invalidGoodwill.accept(Validator) }
    }

}