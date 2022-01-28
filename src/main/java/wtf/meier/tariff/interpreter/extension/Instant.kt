package wtf.meier.tariff.interpreter.extension

import wtf.meier.tariff.interpreter.model.Interval
import java.time.Instant


fun min(d1: Instant, d2: Instant) =
    if (d1.isBefore(d2)) d1 else d2

operator fun Instant.plus(interval: Interval): Instant =
    this.plusMillis(interval.durationMillis())

operator fun Instant.minus(interval: Interval): Instant =
    this.minusMillis(interval.durationMillis())

operator fun Instant.minus(instant: Instant): Instant =
    Instant.ofEpochMilli(this.toEpochMilli() - instant.toEpochMilli())
