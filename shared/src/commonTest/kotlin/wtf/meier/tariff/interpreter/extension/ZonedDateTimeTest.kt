package wtf.meier.tariff.interpreter.extension


import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import wtf.meier.tariff.interpreter.model.ZonedInstant
import kotlin.test.Test
import kotlin.test.assertEquals

class ZonedDateTimeTest {

    private val gmtTimeZoneId = TimeZone.of("GMT")

    @Test
    fun `test min t1 t2 detects minimum correctly`() {
        val t1 = ZonedInstant(2021, 8, 20, 21, 0, 0, 0, gmtTimeZoneId)
        val t2 = ZonedInstant(1988, 8, 20, 21, 0, 0, 0, gmtTimeZoneId)

        assertEquals(min(t1, t2), t2)
    }

    @Test
    fun `test minus operator returns correct millis`() {
        val t1 = ZonedInstant(Instant.fromEpochMilliseconds(5000), gmtTimeZoneId)
        val t2 = ZonedInstant(Instant.fromEpochMilliseconds(1000), gmtTimeZoneId)

        assertEquals(t1 - t2, 4000)
    }
}