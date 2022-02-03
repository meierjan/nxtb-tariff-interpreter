package wtf.meier.tariff.interpreter.model.goodwill

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import wtf.meier.tariff.interpreter.IVisitable
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.IVisitor
import java.time.Instant

@Serializable
sealed class Goodwill:IVisitable {
    // todo do we need this?
    abstract val transparent: Boolean

    abstract override fun accept(visitor: IVisitor)
}

data class ChargedGoodwill(
    val description: String,
    val goodwillStart: Instant,
    val goodwillEnd: Instant,
)

// will be deducted from the end time of the rental
@Serializable
@SerialName("StaticGoodwill")
data class StaticGoodwill(
    val duration: Interval
) : Goodwill() {
    override val transparent = false
    override fun accept(visitor: IVisitor) {
        visitor.visitStaticGoodwill(this)
    }
}

// will be deductee from the start time of the rental
@Serializable
@SerialName("FreeMinutes")
data class FreeMinutes(
    val duration: Interval
) : Goodwill() {
    override val transparent = true
    override fun accept(visitor: IVisitor) {
        visitor.visitFreeMinutes(this)
    }
}

// will be deducted from the end time of the rental
@Serializable
@SerialName("DynamicGoodwill")
data class DynamicGoodwill(
    val deductibleProportionInPercentage: Float
) : Goodwill() {
    override val transparent = false
    override fun accept(visitor: IVisitor) {
        visitor.visitDynamicGoodwill(this)
    }
}