package wtf.meier.tariff.interpreter.extension

import kotlinx.datetime.Instant


fun min(d1: Instant, d2: Instant) =
    if (d1 < d2) d1 else d2

//operator fun Instant.plus(interval: Interval): Instant =
//    this.plus(interval.durationMillis().toDuration(interval.timeUnit))
//
//operator fun Instant.minus(interval: Interval): Instant =
//    this.minus(interval.toDuration())
//
