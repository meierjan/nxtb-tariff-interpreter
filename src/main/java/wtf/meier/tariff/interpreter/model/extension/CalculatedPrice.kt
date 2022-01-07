package wtf.meier.tariff.interpreter.model.extension

import wtf.meier.tariff.interpreter.exception.InconsistentCurrencyException
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.goodwill.ChargedGoodwill
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import java.util.*

fun List<RateCalculator.CalculatedPrice>.toReceipt(
    chargedGoodwill: ChargedGoodwill?,
    currency: Currency
): Receipt {

    if (this.any { it.currency != currency }) {
        throw InconsistentCurrencyException("The passed in currency and rate currencies are not compatible")
    }

    val positionsOfPrice = this.map {
        Receipt.Position(
            price = it.price,
            description = it.description,
            positionStart = it.calculationStart,
            positionEnd = it.calculationEnd
        )
    }

    val optionalGoodwillPositions = chargedGoodwill?.let {
        Receipt.Position(
            description = it.description,
            positionStart = chargedGoodwill.goodwillStart,
            positionEnd = chargedGoodwill.goodwillEnd,
        )
    }

    return Receipt(
        positions = (positionsOfPrice + optionalGoodwillPositions)
            // remove goodwill if null
            .filterNotNull()
            .sortedBy { it.positionStart },
        currency = currency
    )
}
