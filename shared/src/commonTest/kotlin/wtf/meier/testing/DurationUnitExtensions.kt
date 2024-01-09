package wtf.meier.testing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

// Note: As we can't extend the enum class Minutes, we have to workaround like this
// 9.seconds.inWholeSeconds

@OptIn(ExperimentalTime::class)
fun DurationUnit.toSeconds(value: Long): Long =
    Duration.convert(value.toDouble(), this, DurationUnit.SECONDS).toLong()

@OptIn(ExperimentalTime::class)
fun DurationUnit.toMillis(value: Long): Long =
    Duration.convert(value.toDouble(), this, DurationUnit.MILLISECONDS).toLong()

class DurationUnitExtensions {
    @Test
    fun test_minute_to_seconds() {
        assertEquals(
            9.minutes.inWholeSeconds,
            9 * 60
        )
    }

    @Test
    fun test_minute_to_millis() {
        assertEquals(
            DurationUnit.MINUTES.toMillis(9),
            9 * 60 * 1000
        )
    }
}