package wtf.meier.tariff.validator

import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.validator.exception.tariff.InvalidPriceException
import kotlin.test.Test
import kotlin.test.assertFailsWith

class PriceValidatorTest {

    @Test
    fun `Price - negative amount`() {
        val price = Price(-1)

        assertFailsWith<InvalidPriceException> { price.accept(Validator) }
    }
}