package wtf.meier.tariff.interpreter.model.tariff.extension

import wtf.meier.tariff.interpreter.extension.durationMillis
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import java.time.Instant

fun Tariff.getId(): Long {
    return this.id.id
}

fun SlotBasedTariff.Slot.matches(start: Instant, end: Instant): Boolean {
    val duration = end.toEpochMilli() - start.toEpochMilli()

    // we want to exclude users that are riding exactly the amount of where the interval starts
    // so we start counting at interval + 1 ms
    val t1 = this.start.durationMillis() + 1

    return t1 <= duration
}


