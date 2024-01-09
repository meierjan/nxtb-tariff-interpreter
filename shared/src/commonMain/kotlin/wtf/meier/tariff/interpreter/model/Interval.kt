package wtf.meier.tariff.interpreter.model

import kotlinx.serialization.Serializable
import wtf.meier.tariff.serializer.DurationSerializer
import kotlin.time.Duration

typealias Interval = @Serializable(DurationSerializer::class) Duration