package wtf.meier.tariff.interpreter.model

import kotlinx.serialization.Serializable
import wtf.meier.tariff.interpreter.IVisitable
import wtf.meier.tariff.interpreter.IVisitor
import java.util.concurrent.TimeUnit

@Serializable
data class Interval(
    val timeAmount: Int,
    val timeUnit: TimeUnit
) : IVisitable {
    override fun accept(visitor: IVisitor) {
        visitor.visitInterval(this)
    }
}