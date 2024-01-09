package wtf.meier.tariff.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import wtf.meier.tariff.interpreter.model.Currency

object CurrencySerializer : KSerializer<Currency> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Currency", PrimitiveKind.STRING)

    // TODO: handle exception!
    override fun deserialize(decoder: Decoder): Currency = Currency(decoder.decodeString())

    override fun serialize(encoder: Encoder, value: Currency) = encoder.encodeString(value.currencyCode)
}
