package wtf.meier.tariff.interpreter.extension

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

import wtf.meier.tariff.interpreter.model.Interval
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlin.time.toDuration

internal class IntervalKtTest {

    private val interval1 = Interval(timeAmount = 1, timeUnit = TimeUnit.MINUTES)
    private val interval2 = Interval(timeAmount = 2, timeUnit = TimeUnit.MINUTES)
    private val interval3 = Interval(timeUnit = TimeUnit.MINUTES, timeAmount = 5)

    @Test
    fun durationMillis() {
        val result = interval1.durationMillis()

        assertThat(result, equalTo(60000))
    }

    @Test
    fun durationSeconds() {
        val result = interval1.durationSeconds()

        assertThat(result, equalTo(60))
    }

    @Test
    fun minus() {
        val result = interval2 - interval1

        assertThat(result.durationMillis(), equalTo(interval1.durationMillis()))
    }

    @Test
    fun compareTo() {
        val result = interval1 < interval2

        assertThat(result, equalTo(true))
    }

    @Test
    fun div() {
        val result = interval3 / interval2

        assertThat( result, equalTo(2))
    }

    @Test
    fun minOf() {
        val result = minOf(interval1, interval2)

        assertThat(result, equalTo(interval1))
    }

    @Test
    fun toInstant() {
        val result = interval1.toInstant()

        assertThat(result, equalTo(Instant.ofEpochMilli(60000)))
    }

    @Test
    fun times() {
        val result = interval1 * 2
        assertThat(result.durationMillis(), equalTo(interval2.durationMillis()))
    }

    @Test
    fun rem() {
        val result = interval3 % interval2

        assertThat(result.durationMillis(), equalTo(interval1.durationMillis()))
    }

    @Test
    fun ceilDiv(){
        val result = interval3.ceilDiv(interval2)

        assertThat(result, equalTo(3))
    }
}