package wtf.meier.tariff.interpreter.extension

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.time.Instant

class InstantTest {
    val instantOne = Instant.ofEpochMilli(2)
    val instantTwo = Instant.ofEpochMilli(3)

    @Test
    fun `test minus operator`(){
        val difference = instantTwo -  instantOne
        assertThat(difference, equalTo(Instant.ofEpochMilli(1)))
    }

    @Test
    fun min(){
        val min = min(instantOne, instantTwo)
        assertThat(min, equalTo(instantOne))
    }
}