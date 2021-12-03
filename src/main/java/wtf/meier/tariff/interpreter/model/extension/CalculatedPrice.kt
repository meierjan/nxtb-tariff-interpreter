package wtf.meier.tariff.interpreter.model.extension

import wtf.meier.tariff.interpreter.exception.InconsistentCurrencyException
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import java.util.*

fun List<RateCalculator.CalculatedPrice>.toReceipt(currency: Currency): Receipt {
    this.map { if (it.currency !== currency) throw InconsistentCurrencyException("The tariff and rate currencies are not compatible")}
    return Receipt(
        positions = this.map {
            Receipt.Position(
                price = it.price,
                description = it.description,
                positionStart = it.calculationStart,
                positionEnd = it.calculationEnd
            )
        },
        currency = currency
    )
}
