package wtf.meier.tariff.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import wtf.meier.tariff.interpreter.model.Interval
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Serializable
@SerialName("Duration")
private class DurationSurrogate(val timeAmount: Long, val timeUnit: String)

object DurationSerializer : KSerializer<Duration> {

    override val descriptor: SerialDescriptor = DurationSurrogate.serializer().descriptor

    override fun deserialize(decoder: Decoder): Interval {
        val surrogate = decoder.decodeSerializableValue(DurationSurrogate.serializer())
        return surrogate.timeAmount.toDuration(DurationUnit.valueOf(surrogate.timeUnit))
    }

    override fun serialize(encoder: Encoder, value: Interval) {
        val surrogate = DurationSurrogate(
            value.inWholeMilliseconds,
            DurationUnit.MILLISECONDS.name
        )
        encoder.encodeSerializableValue(DurationSurrogate.serializer(), surrogate)
    }
}
