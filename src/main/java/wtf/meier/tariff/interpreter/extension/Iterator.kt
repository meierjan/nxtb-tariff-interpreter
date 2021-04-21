package wtf.meier.tariff.interpreter.extension

class ItemNotFoundException(message: String) : RuntimeException(message)

fun <T> ListIterator<T>.forwardBefore(to: T): ListIterator<T> {


    var itemLastLookedAt: T? = null

    for (currentItem in this) {
        itemLastLookedAt = currentItem
        if (currentItem == to) {
            break
        }
    }

    if (itemLastLookedAt != to) {
        throw ItemNotFoundException("item $to not found in ListIterator")
    }

    // as the iterator is currently pointing at the desired item
    // we have to go one item back to have
    previous()
    return this
}
