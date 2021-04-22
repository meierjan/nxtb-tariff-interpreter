package wtf.meier.tariff.interpreter.util

import org.junit.jupiter.api.Test

class CyclicListTest {

    @Test
    fun `test list works with normal indices`() {
        val list = CyclicList(listOf(0, 1, 2))

        assert(list[0] == 0)
        assert(list[1] == 1)
        assert(list[2] == 2)

    }

    @Test
    fun `test list works with after 1 circle`() {
        val list = CyclicList(listOf(0, 1, 2))

        assert(list[3] == 0)
        assert(list[4] == 1)
        assert(list[5] == 2)

    }
}