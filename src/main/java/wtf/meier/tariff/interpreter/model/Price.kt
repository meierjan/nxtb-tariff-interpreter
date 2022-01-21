package wtf.meier.tariff.interpreter.model

import kotlinx.serialization.Serializable
import wtf.meier.tariff.interpreter.IVisitor

@Serializable
data class Price(
    val credit: Int,
){
    fun accept(visitor: IVisitor) {
        visitor.visitPrice(this)
    }
}
