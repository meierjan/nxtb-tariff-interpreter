package wtf.meier.tariff.interpreter.model.goodwill

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import wtf.meier.tariff.interpreter.model.Interval

@Serializable
sealed class Goodwill {
    abstract val transparent: Boolean
}

// will be deducted from the end time of the rental
@Serializable
@SerialName("StaticGoodwill")
data class StaticGoodwill(
    val duration: Interval
) : Goodwill() {
    override val transparent = false
}

// will be deducted from the start time of the rental
@Serializable
@SerialName("FreeMinutes")
data class FreeMinutes(
    val duration: Interval
) : Goodwill() {
    override val transparent = true
}

// will be deducted from the end time of the rental
@Serializable
@SerialName("DynamicGoodwill")
data class DynamicGoodwill(
    val deductibleProportionInPercentage: Float
) : Goodwill() {
    override val transparent = false
}