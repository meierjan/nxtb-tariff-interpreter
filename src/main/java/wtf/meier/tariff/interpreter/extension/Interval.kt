package wtf.meier.tariff.interpreter.extension

import wtf.meier.tariff.interpreter.model.Interval
import java.util.concurrent.TimeUnit

fun Interval.durationMillis(): Long = timeUnit.toMillis(timeAmount.toLong())

operator fun Interval.minus(start: Interval): Interval =
    Interval((this.durationMillis() - start.durationMillis()).toInt(), TimeUnit.MILLISECONDS)

operator fun Interval.compareTo(interval: Interval): Int =
    (this.durationMillis() - interval.durationMillis()).toInt()

fun minOf(a: Interval, b: Interval): Interval = if (a <= b) a else b


