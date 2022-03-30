package wtf.meier.tariff.interpreter.extension

import wtf.meier.tariff.interpreter.model.Interval
import java.time.Instant
import java.util.concurrent.TimeUnit

fun Interval.durationMillis(): Long = timeUnit.toMillis(timeAmount.toLong())

fun Interval.durationSeconds(): Long = timeUnit.toSeconds(timeAmount.toLong())

operator fun Interval.minus(start: Interval): Interval =
    Interval((this.durationMillis() - start.durationMillis()).toInt(), TimeUnit.MILLISECONDS)

operator fun Interval.compareTo(interval: Interval): Int =
    (this.durationMillis() - interval.durationMillis()).toInt()

operator fun Interval.div(interval: Interval): Long =
    this.durationMillis() / interval.durationMillis()

fun Interval.ceilDiv(interval: Interval): Long =
    kotlin.math.ceil(this.durationMillis().toFloat() / interval.durationMillis().toFloat()).toLong()

operator fun Interval.times(b: Long): Interval =
    Interval((this.durationMillis() * b).toInt(), TimeUnit.MILLISECONDS)

operator fun Interval.rem(interval: Interval): Interval =
    Interval((this.durationMillis() % interval.durationMillis()).toInt(), TimeUnit.MILLISECONDS)

fun minOf(a: Interval, b: Interval): Interval = if (a <= b) a else b

fun Interval.toInstant(): Instant = Instant.ofEpochMilli(this.timeUnit.toMillis(this.timeAmount.toLong()))