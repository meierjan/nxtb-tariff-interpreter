package wtf.meier.tariff.api.repository

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId

interface ITariffRepository {
    fun getById(id: TariffId): Mono<Tariff>
    fun getAll():Flux<Tariff>
    fun create(tariff: Tariff):Mono<Tariff>
    fun update(tariff: Tariff):Mono<Tariff>
    fun deleteById(id: TariffId)
}