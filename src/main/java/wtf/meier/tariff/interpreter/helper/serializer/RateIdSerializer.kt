package wtf.meier.tariff.interpreter.helper.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import wtf.meier.tariff.interpreter.model.rate.RateId
import java.util.*

object RateIdSerializer : KSerializer<RateId> {

    override fun deserialize(decoder: Decoder):RateId = RateId(decoder.decodeLong())

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("RateId", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: RateId) = encoder.encodeString(value.toString())
}
