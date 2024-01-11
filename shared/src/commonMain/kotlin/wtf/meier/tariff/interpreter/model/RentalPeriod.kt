package wtf.meier.tariff.interpreter.model

import kotlinx.datetime.Instant
import wtf.meier.tariff.interpreter.IVisitable
import wtf.meier.tariff.interpreter.IVisitor
import wtf.meier.tariff.interpreter.model.goodwill.ChargedGoodwill

data class RentalPeriod(
    val rentalStart: Instant = Instant.fromEpochMilliseconds(0),
    val rentalEnd: Instant,
    val invoicedStart: Instant = rentalStart,
    val invoicedEnd: Instant = rentalEnd,
    val chargedGoodwill: ChargedGoodwill? = null
) : IVisitable {
    val duration: Interval
        get() = invoicedEnd - invoicedStart

    override fun accept(visitor: IVisitor) {
        visitor.visitRentalPeriod(this)
    }
}
