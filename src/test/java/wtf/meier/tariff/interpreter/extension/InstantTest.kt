package wtf.meier.tariff.interpreter.extension

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.time.Instant

class InstantTest {
    @Test
    fun `test minus operator`(){
        val difference = Instant.ofEpochMilli(1) - Instant.ofEpochSecond(0)
        print(difference.toEpochMilli())
        assertThat(difference.toEpochMilli(), equalTo(1))
    }
}