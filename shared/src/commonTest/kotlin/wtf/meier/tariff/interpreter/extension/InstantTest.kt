package wtf.meier.tariff.interpreter.extension


import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.milliseconds

class InstantTest {
    private val instantOne = Instant.fromEpochMilliseconds(2)
    private val instantTwo = Instant.fromEpochMilliseconds(3)

    @Test
    fun `test minus operator`() {
        val difference = instantTwo - instantOne
        assertEquals(difference, 1.milliseconds)
    }

    @Test
    fun min() {
        val min = min(instantOne, instantTwo)
        assertEquals(min, instantOne)
    }
}