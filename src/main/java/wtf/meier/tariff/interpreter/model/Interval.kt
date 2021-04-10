package wtf.meier.tariff.interpreter.model

import java.util.concurrent.TimeUnit

data class Interval(
    val timeAmount: Int,
    val timeUnit: TimeUnit
)