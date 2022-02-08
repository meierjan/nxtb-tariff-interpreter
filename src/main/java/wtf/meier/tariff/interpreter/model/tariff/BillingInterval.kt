package wtf.meier.tariff.interpreter.model.tariff

import kotlinx.serialization.Serializable
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price

@Serializable
data class BillingInterval(val duration: Interval, val maxPrice: Price = Price(Int.MAX_VALUE))