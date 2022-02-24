package wtf.meier.tariff.api.route

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import wtf.meier.tariff.api.service.calculation.RateCalculationService
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.Instant

@RestController
class CalculateRoute(
    @Autowired
    private var calculationService: RateCalculationService
) {
    @GetMapping("/calculate")
    fun calculate(
        @RequestParam(value = "tariffId") tariffIdLong: Long,
        @RequestParam(value = "from") startUnixTimeStamp: Long,
        @RequestParam(value = "to") endUnixTimeStamp: Long
    ): Mono<Receipt> {

        val tariffId = TariffId(tariffIdLong)
        val from = Instant.ofEpochSecond(startUnixTimeStamp)
        val to = Instant.ofEpochSecond(endUnixTimeStamp)

        return calculationService.calculateRate(tariffId, from, to)
    }

}