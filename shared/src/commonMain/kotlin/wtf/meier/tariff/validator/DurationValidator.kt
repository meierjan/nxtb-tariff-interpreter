package wtf.meier.tariff.validator

import wtf.meier.tariff.interpreter.IVisitor
import kotlin.time.Duration

fun Duration.accept(visitor: IVisitor) {
    visitor.visitDuration(this)
}