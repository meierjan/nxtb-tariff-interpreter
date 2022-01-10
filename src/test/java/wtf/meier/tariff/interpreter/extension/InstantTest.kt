package wtf.meier.tariff.interpreter.extension

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import java.time.Instant
import java.util.concurrent.TimeUnit

class InstantTest {
    private val instantOne = Instant.ofEpochMilli(2)
    private val instantTwo = Instant.ofEpochMilli(3)
    private val intervalOne = Interval(timeAmount = 2, timeUnit = TimeUnit.MILLISECONDS)

    @Test
    fun `test instant minus operator`() {
        val difference = instantTwo - instantOne
        assertThat(difference, equalTo(Instant.ofEpochMilli(1)))
    }

    @Test
    fun `test instant - interval operator`() {
        val difference = instantOne - intervalOne

        assertThat(difference, equalTo(Instant.ofEpochMilli(0)))
    }

    @Test
    fun `test instant + interval operator`() {
        val sum = instantOne + intervalOne

        assertThat(sum, equalTo(Instant.ofEpochMilli(4)))
    }


    @Test
    fun min() {
        val min = min(instantOne, instantTwo)
        assertThat(min, equalTo(instantOne))
    }
}