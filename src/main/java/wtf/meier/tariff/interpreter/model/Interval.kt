package wtf.meier.tariff.interpreter.model

import kotlinx.serialization.Serializable
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff
import java.util.concurrent.TimeUnit

@Serializable
data class Interval(
    val timeAmount: Int,
    val timeUnit: TimeUnit
) {
    operator fun minus(start: Interval):Interval {
        return Interval((timeUnit.toMillis(timeAmount.toLong()) - start.timeUnit.toMillis(start.timeAmount.toLong())).toInt(), TimeUnit.MILLISECONDS)
    }
}