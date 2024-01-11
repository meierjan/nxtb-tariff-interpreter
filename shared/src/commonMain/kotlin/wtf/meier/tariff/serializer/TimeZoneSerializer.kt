package wtf.meier.tariff.serializer

import kotlinx.datetime.TimeZone
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TimeZoneSerializer : KSerializer<TimeZone> {

    //Todo: handle exception
    override fun deserialize(decoder: Decoder): TimeZone = TimeZone.of(decoder.decodeString())

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TimeZone", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TimeZone) = encoder.encodeString(value.id)

}
