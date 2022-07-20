package wtf.meier.tariff.validator

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.validator.exception.tariff.InvalidPriceException

class PriceValidatorTest {

    @Test
    fun `Price - negative amount`() {
        val price = Price(-1)

        assertThrows<InvalidPriceException> { price.accept(Validator) }
    }
}