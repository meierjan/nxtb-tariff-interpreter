package wtf.meier.tariff.interpreter.serializer

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import wtf.meier.tariff.interpreter.model.goodwill.DynamicGoodwill
import wtf.meier.tariff.interpreter.model.goodwill.FreeMinutes
import wtf.meier.tariff.interpreter.model.goodwill.Goodwill
import wtf.meier.tariff.interpreter.model.goodwill.StaticGoodwill
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.Rate
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff

object TariffDeserializer {

    private val module = SerializersModule {
        polymorphic(Rate::class) {
            subclass(FixedRate::class)
            subclass(TimeBasedRate::class)
        }
        polymorphic(Tariff::class) {
            subclass(SlotBasedTariff::class)
            subclass(TimeBasedTariff::class)
        }
        polymorphic(Goodwill::class) {
            subclass(FreeMinutes::class)
            subclass(StaticGoodwill::class)
            subclass(DynamicGoodwill::class)
        }
    }

    private val format = Json {
        serializersModule = module
        classDiscriminator = "type"
    }

    fun deserializeTariff(serializedTariff: String): Tariff = format.decodeFromString(serializedTariff)
}