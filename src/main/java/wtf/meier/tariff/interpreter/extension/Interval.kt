package wtf.meier.tariff.interpreter.extension

import wtf.meier.tariff.interpreter.model.Interval

fun Interval.durationMillis(): Long = timeUnit.toMillis(timeAmount.toLong())
