package wtf.meier.tariff.api.route

import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import wtf.meier.tariff.api.service.tariff.MockTariffServiceImpl
import wtf.meier.tariff.api.service.tariff.TariffService
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId

@RestController
@RequestMapping("/tariff")
class TariffRoute {

    private var tariffService: TariffService = MockTariffServiceImpl()

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
        tariffService.setTariff(tariff)

    @DeleteMapping("/{id}")
    fun deleteTariff(@PathVariable id: TariffId): Mono<TariffId> =
        tariffService.deleteTariff(id)

    @GetMapping("/validate")
    fun validateTariff(@RequestBody tariff: Tariff): Mono<Tariff> =
        // TODO!
        Mono.just(tariff)
}