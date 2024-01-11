package wtf.meier.tariff.interpreter.extension

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IteratorTest {
    @Test
    fun `test fastForward forwards correctly with basic list`() {
        val listIterator = listOf("jan", "ist", "sehr", "blau").listIterator()

        listIterator.forwardBefore("sehr")

        assertEquals(listIterator.next(), "sehr")
    }

    @Test
    fun `test fastForward contains only one element`() {
        val listIterator = listOf("jonas").listIterator()

        listIterator.forwardBefore("jonas")

        assertEquals(listIterator.next(), "jonas")
    }

    @Test
    fun `test fastForward does not contain element`() {
        val listIterator = listOf("jan", "ist", "sehr", "blau").listIterator()

        val exception = assertFailsWith<ItemNotFoundException> {
            listIterator.forwardBefore("jonas")
        }

        assertEquals("item jonas not found in ListIterator", exception.message)

    }
}