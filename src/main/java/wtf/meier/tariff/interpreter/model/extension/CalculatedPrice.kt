package wtf.meier.tariff.interpreter.model.extension

import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.rate.RateCalculator

fun List<RateCalculator.CalculatedPrice>.toReceipt() : Receipt =
    Receipt(
        positions = this.map {
            Receipt.Position(
                price = it.price,
                description = it.description
            )
        },
        // TODO: find a better way here
        currency = this.first().currency

    )
