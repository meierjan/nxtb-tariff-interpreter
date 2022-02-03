package wtf.meier.tariff.api.service

import reactor.core.publisher.Mono
import wtf.meier.tariff.api.service.tariff.TariffService
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.tariff.TariffCalculator
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.Instant

class RateCalculationServiceImpl(
  private val tariffService: TariffService,
  private val tariffCalculator: TariffCalculator = TariffCalculator()
) : RateCalculationService {

  override fun calculateRate(tariffId: TariffId, from: Instant, to: Instant): Mono<Receipt> =
    tariffService.getTariff(tariffId)
      .map { tariff -> tariffCalculator.calculate(tariff, RentalPeriod(from, to)) }

}

