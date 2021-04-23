package wtf.meier.tariff.api.service

import reactor.core.publisher.Mono
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.Instant

interface RateCalculationService {

  fun calculateRate(tariffId: TariffId, from: Instant, to: Instant): Mono<Receipt>

}