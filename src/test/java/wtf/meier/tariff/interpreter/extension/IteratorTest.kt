package wtf.meier.tariff.interpreter.extension

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IteratorTest {
    @Test
    fun `test fastForward forwards correctly with basic list`() {
        val listIterator = listOf("jan", "ist", "sehr", "blau").listIterator()

        listIterator.forwardBefore("sehr")

        assert(listIterator.next() == "sehr")
    }

    @Test
    fun `test fastForward contains only one element`() {
        val listIterator = listOf("jonas").listIterator()

        listIterator.forwardBefore("jonas")

        assert(listIterator.next() == "jonas")
    }

    @Test
    fun `test fastForward does not contain element`() {
        val listIterator = listOf("jan", "ist", "sehr", "blau").listIterator()

        val exception = Assertions.assertThrows(ItemNotFoundException::class.java) {
            listIterator.forwardBefore("jonas")
        }

        assertEquals("item jonas not found in ListIterator", exception.message)

    }
}