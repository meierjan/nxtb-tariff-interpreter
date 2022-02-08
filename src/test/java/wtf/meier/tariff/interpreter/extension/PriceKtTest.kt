package wtf.meier.tariff.interpreter.extension

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Price

internal class PriceKtTest {

    val price1 = Price(1)
    val price2 = Price(2)


    @Test
    fun minOf() {
        val result = minOf(price1, price2)

        assertThat(result, equalTo(price1))
    }

    @Test
    fun maxOf() {
        val result = maxOf(price1, price2)

        assertThat(result, equalTo(price2))
    }

    @Test
    fun minus() {
        val result = price2 - price1

        assertThat(result, equalTo(price1))
    }
}