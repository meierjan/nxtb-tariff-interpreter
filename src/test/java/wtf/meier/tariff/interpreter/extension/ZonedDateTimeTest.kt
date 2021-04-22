package wtf.meier.tariff.interpreter.extension

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class ZonedDateTimeTest {

    private val gmtTimeZoneId = ZoneId.of("GMT")

    @Test
    fun `test min(t1,t2) detects minimum correctly`() {
        val t1 = ZonedDateTime.of(2021, 8, 20, 21, 0, 0, 0, gmtTimeZoneId)
        val t2 = ZonedDateTime.of(1988, 8, 20, 21, 0, 0, 0, gmtTimeZoneId)

        assertThat(min(t1, t2), equalTo(t2))
    }

    @Test
    fun `test minus operator returns correct millis`() {
        val t1 = ZonedDateTime.ofInstant(Instant.ofEpochMilli(5000), gmtTimeZoneId)
        val t2 = ZonedDateTime.ofInstant(Instant.ofEpochMilli(1000), gmtTimeZoneId)

        assertThat(t1 - t2, equalTo(4000))
    }
}