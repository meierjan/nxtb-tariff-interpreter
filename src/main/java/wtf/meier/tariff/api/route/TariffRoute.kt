package wtf.meier.tariff.api.route

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import wtf.meier.tariff.api.service.tariff.ITariffService
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId

@RestController
@RequestMapping("/tariff")
class TariffRoute(@Autowired @Qualifier("TariffService") val tariffService: ITariffService) {

    @PostMapping
    fun createTariff(@RequestBody tariff: Tariff): Mono<Tariff> =
        tariffService.createTariff(tariff)

    @GetMapping("/{id}")
    fun getTariff(@PathVariable id: TariffId): Mono<Tariff> =
        tariffService.getTariff(id)

    @GetMapping
    fun getAllTariffs(): Flux<Tariff> =
        tariffService.getAllTariffs()

    @PutMapping
    fun setTariff(@RequestBody tariff: Tariff): Mono<Tariff> =
        tariffService.updateTariff(tariff)

    @DeleteMapping("/{id}")
    fun deleteTariff(@PathVariable id: TariffId) {
        tariffService.deleteTariff(id)
    }

    @GetMapping("/validate")
    fun validateTariff(@RequestBody tariff: Tariff): Mono<Tariff> =
        tariffService.validateTariff(tariff)
}