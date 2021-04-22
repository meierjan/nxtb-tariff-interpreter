package wtf.meier.tariff.interpreter.util

// taken from https://github.com/tginsberg/cirkle

class CyclicList<out T>(private val list: List<T>) : List<T> by list {

    override fun get(index: Int): T =
            list[index.safely()]

    override fun listIterator(index: Int): ListIterator<T> =
            list.listIterator(index.safely())


    private fun Int.safely(): Int =
            if (this < 0) (this % size + size) % size
            else this % size
}