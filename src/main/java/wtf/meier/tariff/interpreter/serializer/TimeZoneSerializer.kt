package wtf.meier.tariff.interpreter.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.ZoneId
import java.util.*

object TimeZoneSerializer : KSerializer<TimeZone> {

    override fun deserialize(decoder: Decoder): TimeZone = TimeZone.getTimeZone(ZoneId.of(decoder.decodeString()))

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("TimeZone", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: TimeZone) = encoder.encodeString(value.toZoneId().toString())

}
