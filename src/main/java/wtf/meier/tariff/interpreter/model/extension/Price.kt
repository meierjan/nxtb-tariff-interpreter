package wtf.meier.tariff.interpreter.model.extension

import wtf.meier.tariff.interpreter.model.Price


operator fun Price.times(times: Int): Price =
    Price(times * credit)

operator fun Price.plus(p2: Price): Price =
    Price(credit + p2.credit)


fun max(p1: Price, p2: Price): Price {
    return if (p1.credit < p2.credit) {
        p2
    } else {
        p1
    }
}

fun min(p1: Price, p2: Price): Price {
    return if (p1.credit < p2.credit) {
        p1
    } else {
        p2
    }
}
