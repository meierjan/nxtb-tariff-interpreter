package wtf.meier.tariff.api.service.tariff

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import wtf.meier.tariff.api.repository.ITariffRepository
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId

@Service
@Qualifier("TariffService")
class TariffService(@Autowired @Qualifier("TariffRepository") override val tariffRepository: ITariffRepository) :
    ITariffService {

    override fun getTariff(tariffId: TariffId): Mono<Tariff> =
        tariffRepository.getById(tariffId)

    override fun getAllTariffs(): Flux<Tariff> =
        tariffRepository.getAll()

    override fun updateTariff(tariff: Tariff): Mono<Tariff> =
        // TODO validation!
        tariffRepository.update(tariff)

    override fun createTariff(tariff: Tariff): Mono<Tariff> {
        // TODO validation!
        return tariffRepository.create(tariff)
    }

    override fun deleteTariff(tariffId: TariffId) {
        tariffRepository.deleteById(tariffId)
    }

    override fun validateTariff(tariff: Tariff): Mono<Tariff> {
        TODO("Not yet implemented")
    }
}