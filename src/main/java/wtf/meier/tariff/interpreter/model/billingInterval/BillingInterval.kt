package wtf.meier.tariff.interpreter.model.billingInterval

import kotlinx.serialization.Serializable
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price

@Serializable
data class BillingInterval(val duration: Interval, val maxPrice:Price)