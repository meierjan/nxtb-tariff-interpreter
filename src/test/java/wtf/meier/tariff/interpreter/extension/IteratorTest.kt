package wtf.meier.tariff.interpreter.extension

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class IteratorTest {
    @Test
    fun `test fastForward forwards correctly with basic list`() {
        val listIterator = listOf("jan", "ist", "sehr", "blau").listIterator()

        listIterator.forwardBefore("sehr")

        assertThat(listIterator.next(), equalTo("sehr"))
    }

    @Test
    fun `test fastForward contains only one element`() {
        val listIterator = listOf("jonas").listIterator()

        listIterator.forwardBefore("jonas")

        assertThat(listIterator.next(), equalTo("jonas"))
    }

    @Test
    fun `test fastForward does not contain element`() {
        val listIterator = listOf("jan", "ist", "sehr", "blau").listIterator()

        val exception = Assertions.assertThrows(ItemNotFoundException::class.java) {
            listIterator.forwardBefore("jonas")
        }

        assertThat("item jonas not found in ListIterator", equalTo(exception.message))

    }
}