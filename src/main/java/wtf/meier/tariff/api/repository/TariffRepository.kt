package wtf.meier.tariff.api.repository

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import wtf.meier.tariff.api.repository.exception.TariffAlreadyExistsException
import wtf.meier.tariff.api.repository.exception.TariffNotFoundException
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.tariff.interpreter.model.tariff.extension.getId
import wtf.meier.tariff.serializer.TariffDeserializer
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Lob

@Entity
data class JsonTariff(
    @Id val id: Long? = null, @Lob val tariff: String? = null
)

@Controller
@Qualifier("TariffRepository")
class TariffRepository(
    @Autowired private val repository: DBRepository
) : ITariffRepository {

    override fun getById(id: TariffId): Mono<Tariff> {
        val tariff = repository.findById(id.id)
        if (tariff.isEmpty) return Mono.error(TariffNotFoundException(id))
        return Mono.just(TariffDeserializer.deserializeTariff(repository.findById(id.id).get().tariff!!))
    }

    override fun getAll(): Flux<Tariff> {
        val allTariffs = repository.findAll().map { t ->
            TariffDeserializer.deserializeTariff(
                t.tariff ?: throw RuntimeException("Can't find any tariff")
            )
        }
        return Flux.fromIterable(allTariffs)
    }

    override fun create(tariff: Tariff): Mono<Tariff> {
        return if (repository.existsById(tariff.getId())) Mono.error(TariffAlreadyExistsException(tariff.id)) else
            Mono.just(
                TariffDeserializer.deserializeTariff(
                    repository.save(
                        JsonTariff(
                            id = tariff.id.id, tariff = TariffDeserializer.serializeTariff(tariff)
                        )
                    ).tariff!!
                )
            )
    }

    override fun update(tariff: Tariff): Mono<Tariff> {
        if (!repository.existsById(tariff.getId())) return Mono.error(TariffNotFoundException(tariff.id))
        return Mono.just(
            TariffDeserializer.deserializeTariff(
                repository.save(
                    JsonTariff(
                        tariff.getId(), TariffDeserializer.serializeTariff(tariff)
                    )
                ).tariff!!
            )
        )
    }

    override fun deleteById(id: TariffId) {
        if (!repository.existsById(id.id)) throw TariffNotFoundException(id)
        repository.deleteById(id.id)
    }
}

@Repository
interface DBRepository : CrudRepository<JsonTariff, Long>
