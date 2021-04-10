package wtf.meier.tariff.interpreter.extension

import wtf.meier.tariff.interpreter.model.Interval

fun Interval.toMillis(): Long = timeUnit.toMillis(timeAmount.toLong())
