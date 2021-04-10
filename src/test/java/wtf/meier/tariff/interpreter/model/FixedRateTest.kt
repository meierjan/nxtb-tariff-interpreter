package wtf.meier.tariff.interpreter.model

import org.junit.jupiter.api.Test
import java.util.*


class FixedRateTest {

    @Test
    fun `Test Fixed Rate Tariff`() {
        val rate = FixedRate(
            id = RateId(100),
            currency = Currency.getInstance("EUR"),
            price = Price(1000)
        )


        val price = rate.calculate(Date(), Date(1337))

        assert(price.credit == 1000L)
    }


}