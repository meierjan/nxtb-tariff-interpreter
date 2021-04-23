package wtf.meier.tariff.interpreter.util

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class CyclicListTest {

    @Test
    fun `test list works with normal indices`() {
        val list = CyclicList(listOf(0, 1, 2))

        assertThat(list[0], equalTo( 0))
        assertThat(list[1], equalTo( 1))
        assertThat(list[2], equalTo( 2))
    }

    @Test
    fun `test list works with after 1 circle`() {
        val list = CyclicList(listOf(0, 1, 2))

        assertThat(list[3], equalTo( 0))
        assertThat(list[4], equalTo( 1))
        assertThat(list[5], equalTo( 2))
    }

    @Test
    fun `test listIterator returns proper elements`() {
        val list = CyclicList(listOf(0, 1, 2)).listIterator()

        assertThat(list.next(), equalTo(0))
        assertThat(list.next(), equalTo(1))
        assertThat(list.next(), equalTo(2))

    }
}