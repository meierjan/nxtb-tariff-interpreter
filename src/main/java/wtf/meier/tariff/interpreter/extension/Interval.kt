package wtf.meier.tariff.interpreter.extension

import wtf.meier.tariff.interpreter.model.Interval
import java.util.concurrent.TimeUnit

fun Interval.durationMillis(): Long = timeUnit.toMillis(timeAmount.toLong())

operator fun Interval.minus(start: Interval): Interval = Interval(
    (timeUnit.toMillis(timeAmount.toLong()) - start.timeUnit.toMillis(start.timeAmount.toLong())).toInt(),
    TimeUnit.MILLISECONDS
)

