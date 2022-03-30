package wtf.meier.tariff.interpreter.util

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class CyclicListIteractorTest {

    @Test
    fun `test list works with normal indices`() {
        val list = CyclicListIterator(listOf(0, 1, 2))

        assertThat(list.next(), equalTo( 0))
        assertThat(list.next(), equalTo( 1))
        assertThat(list.next(), equalTo( 2))
    }

    @Test
    fun `test list works with after 1 circle`() {
        val list = CyclicListIterator(listOf(0, 1, 2))

        assertThat(list.next(), equalTo( 0))
        assertThat(list.next(), equalTo( 1))
        assertThat(list.next(), equalTo( 2))
    }

    @Test
    fun `test listIterator returns proper elements cycling forward`() {
        val list = CyclicListIterator(listOf(0, 1, 2))

        assertThat(list.next(), equalTo(0))
        assertThat(list.next(), equalTo(1))
        assertThat(list.next(), equalTo(2))
        assertThat(list.next(), equalTo(0))
        assertThat(list.next(), equalTo(1))
        assertThat(list.next(), equalTo(2))

    }


    @Test
    fun `test listIterator returns proper elements cycling backward`() {
        val list = CyclicListIterator(listOf(0, 1, 2))

        assertThat(list.previous(), equalTo(2))
        assertThat(list.previous(), equalTo(1))
        assertThat(list.previous(), equalTo(0))
        assertThat(list.previous(), equalTo(2))
        assertThat(list.previous(), equalTo(1))
        assertThat(list.previous(), equalTo(0))

    }
}