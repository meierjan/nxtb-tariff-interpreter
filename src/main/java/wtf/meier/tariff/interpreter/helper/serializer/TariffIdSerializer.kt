package wtf.meier.tariff.interpreter.helper.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import wtf.meier.tariff.interpreter.model.tariff.TariffId

object TariffIdSerializer : KSerializer<TariffId> {

    override fun deserialize(decoder: Decoder): TariffId = TariffId(decoder.decodeLong())

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Tariff", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: TariffId) = encoder.encodeString(value.toString())
}
