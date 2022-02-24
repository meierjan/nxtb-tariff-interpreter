package wtf.meier.tariff.api.service.calculation

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import wtf.meier.tariff.api.service.tariff.ITariffService
import wtf.meier.tariff.interpreter.Calculator
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.Instant


@Service
class RateCalculationService(
    private val tariffService: ITariffService,
    private val tariffCalculator: Calculator = Calculator()
) : IRateCalculationService {

    override fun calculateRate(tariffId: TariffId, from: Instant, to: Instant): Mono<Receipt> =
        tariffService.getTariff(tariffId)
            .map { tariff -> tariffCalculator.calculate(tariff, RentalPeriod(from, to)) }

}

