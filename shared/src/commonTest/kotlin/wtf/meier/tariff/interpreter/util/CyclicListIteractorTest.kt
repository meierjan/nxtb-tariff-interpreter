package wtf.meier.tariff.interpreter.util

import kotlin.test.Test
import kotlin.test.assertEquals


class CyclicListIteractorTest {

    @Test
    fun `test list works with normal indices`() {
        val list = CyclicListIterator(listOf(0, 1, 2))

        assertEquals(list.next(), 0)
        assertEquals(list.next(), 1)
        assertEquals(list.next(), 2)
    }

    @Test
    fun `test list works with after 1 circle`() {
        val list = CyclicListIterator(listOf(0, 1, 2))

        assertEquals(list.next(), 0)
        assertEquals(list.next(), 1)
        assertEquals(list.next(), 2)
    }

    @Test
    fun `test listIterator returns proper elements cycling forward`() {
        val list = CyclicListIterator(listOf(0, 1, 2))

        assertEquals(list.next(), 0)
        assertEquals(list.next(), 1)
        assertEquals(list.next(), 2)
        assertEquals(list.next(), 0)
        assertEquals(list.next(), 1)
        assertEquals(list.next(), 2)

    }


    @Test
    fun `test listIterator returns proper elements cycling backward`() {
        val list = CyclicListIterator(listOf(0, 1, 2))

        assertEquals(list.previous(), 2)
        assertEquals(list.previous(), 1)
        assertEquals(list.previous(), 0)
        assertEquals(list.previous(), 2)
        assertEquals(list.previous(), 1)
        assertEquals(list.previous(), 0)

    }
}