package wtf.meier.tariff.interpreter.model

import kotlinx.serialization.Serializable
import java.util.concurrent.TimeUnit

@Serializable
data class Interval(
    val timeAmount: Int,
    val timeUnit: TimeUnit
)