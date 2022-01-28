package wtf.meier.tariff.validator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.goodwill.DynamicGoodwill
import wtf.meier.tariff.interpreter.model.goodwill.FreeMinutes
import wtf.meier.tariff.interpreter.model.goodwill.Goodwill
import wtf.meier.tariff.interpreter.model.goodwill.StaticGoodwill
import wtf.meier.tariff.validator.exception.tariff.InvalidDynamicGoodwillException
import wtf.meier.tariff.validator.exception.tariff.InvalidIntervalException
import java.util.concurrent.TimeUnit

class GoodwillValidatorTest {

    lateinit var invalidGoodwill: Goodwill

    @Test
    fun `DynamicGoodwill - negative percentages`() {
        invalidGoodwill = DynamicGoodwill(
            deductibleProportionInPercentage = -Float.MIN_VALUE
        )
        assertThrows<InvalidDynamicGoodwillException> { invalidGoodwill.accept(Validator) }

        invalidGoodwill = DynamicGoodwill(
            deductibleProportionInPercentage = -Float.MAX_VALUE
        )
        assertThrows<InvalidDynamicGoodwillException> { invalidGoodwill.accept(Validator) }
    }

    @Test
    fun `DynamicGoodwill - over 100 percent`() {
        invalidGoodwill = DynamicGoodwill(
            deductibleProportionInPercentage = 100.1f
        )
        assertThrows<InvalidDynamicGoodwillException> { invalidGoodwill.accept(Validator) }

        invalidGoodwill = DynamicGoodwill(
            deductibleProportionInPercentage = Float.MAX_VALUE
        )
        assertThrows<InvalidDynamicGoodwillException> { invalidGoodwill.accept(Validator) }
    }

    @Test
    fun `DynamicGoodwill - valid percentages`() {
        for (i in 0..100) {
            invalidGoodwill = DynamicGoodwill(
                deductibleProportionInPercentage = i.toFloat()
            )
            invalidGoodwill.accept(Validator)
        }
    }

    @Test
    fun `StaticGoodwill - negative Interval`() {
        invalidGoodwill = StaticGoodwill(
            duration = Interval(-100, TimeUnit.MINUTES)
        )
        assertThrows<InvalidIntervalException> { invalidGoodwill.accept(Validator) }
    }

    @Test
    fun `FreeMinutes - negative Interval`() {
        invalidGoodwill = FreeMinutes(
            duration = Interval(-100, TimeUnit.MINUTES)
        )
        assertThrows<InvalidIntervalException> { invalidGoodwill.accept(Validator) }
    }

}