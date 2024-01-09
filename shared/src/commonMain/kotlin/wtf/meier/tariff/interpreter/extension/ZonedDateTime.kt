package wtf.meier.tariff.interpreter.extension

import wtf.meier.tariff.interpreter.model.ZonedInstant


fun min(t1: ZonedInstant, t2: ZonedInstant) = if (t1 < t2) {
    t1
} else {
    t2
}

private operator fun ZonedInstant.compareTo(other: ZonedInstant): Int {
    if (this.zone != other.zone) {
        throw IllegalArgumentException("Cannot compare ZonedInstants with different timezones")
    }
    return this.instant.compareTo(other.instant)
}


operator fun ZonedInstant.minus(other: ZonedInstant): Long {
    if (this.zone != other.zone) {
        throw IllegalArgumentException("Cannot compare ZonedInstants with different timezones")
    }
    return (this.instant - other.instant).inWholeMilliseconds
}

//operator fun LocalDateTime.minus(slotStart: LocalDateTime): Long =
//    (this.toInstant(TimeZone.UTC) - slotStart.toInstant(TimeZone.UTC)).inWholeMilliseconds
