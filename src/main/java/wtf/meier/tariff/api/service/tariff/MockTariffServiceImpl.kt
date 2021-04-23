package wtf.meier.tariff.api.service.tariff

import reactor.core.publisher.Mono
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId

class TariffNotFoundException(tariffId: TariffId) :
  RuntimeException("tariff with if $tariffId wasn't found")

class MockTariffServiceImpl : TariffService {

  private val tariffMap: Map<TariffId, Tariff> = emptyMap()

  override fun getTariff(tariffId: TariffId): Mono<Tariff> =
    Mono.create { tariffMap[tariffId] ?: throw TariffNotFoundException(tariffId) }
}