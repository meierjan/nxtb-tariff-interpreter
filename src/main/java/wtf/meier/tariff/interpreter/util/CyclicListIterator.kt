package wtf.meier.tariff.interpreter.util

// taken from https://github.com/tginsberg/cirkle

class CyclicListIterator<out T>(private val list: List<T>) : ListIterator<T> {

    private var index = 0

    override fun hasNext(): Boolean = true

    override fun hasPrevious(): Boolean = false

    override fun next(): T {
        val item = list[index]
        index = nextIndex()
        return item
    }

    override fun nextIndex(): Int =
        (index + 1) % list.size

    override fun previous(): T {
        index = previousIndex()
        return list[index]
    }

    override fun previousIndex(): Int =
        (list.size + index - 1) % list.size

}