package wtf.meier.tariff.api.service.tariff

import reactor.core.publisher.Mono
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId

interface TariffService {
  fun getTariff(tariffId: TariffId) : Mono<Tariff>
}