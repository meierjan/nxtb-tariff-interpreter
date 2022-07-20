package wtf.meier.tariff.api.service.calculation

import reactor.core.publisher.Mono
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.Instant

interface IRateCalculationService {

  fun calculateRate(tariffId: TariffId, from: Instant, to: Instant): Mono<Receipt>

}