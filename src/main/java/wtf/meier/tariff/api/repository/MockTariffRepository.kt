package wtf.meier.tariff.api.repository

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import wtf.meier.tariff.api.repository.exception.TariffAlreadyExistsException
import wtf.meier.tariff.api.repository.exception.TariffNotFoundException
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff
import java.time.DayOfWeek
import java.time.ZoneId
import java.util.*

@Component
@Qualifier("MockTariffRepository")
class MockTariffRepository : ITariffRepository {

    override fun getById(id: TariffId): Mono<Tariff> =
        Mono.fromCallable { tariffMap[id] ?: throw (TariffNotFoundException(id)) }

    override fun getAll(): Flux<Tariff> =
        Flux.fromIterable(tariffMap.values)


    override fun create(tariff: Tariff): Mono<Tariff> {
        if (tariffMap[tariff.id] != null) return Mono.error(TariffAlreadyExistsException(tariff.id))
        tariffMap[tariff.id] = tariff
        return Mono.just(tariffMap[tariff.id]!!)
    }

    override fun update(tariff: Tariff): Mono<Tariff> {
        if (tariffMap[tariff.id] == null) return Mono.error(TariffNotFoundException(tariff.id))
        tariffMap[tariff.id] = tariff
        return Mono.just(tariffMap[tariff.id]!!)
    }

    override fun deleteById(id: TariffId) {
        tariffMap.remove(id);
    }

    private val tariffMap: MutableMap<TariffId, Tariff> = mutableMapOf()

    private val timezone = TimeZone.getTimeZone(ZoneId.of("GMT"))

    private val rate1 = FixedRate(
        id = RateId(1),
        currency = Currency.getInstance("EUR"),
        price = Price(100)
    )

    private val rate2 = FixedRate(
        id = RateId(2),
        currency = Currency.getInstance("EUR"),
        price = Price(50)
    )

    /**
     * tariff:
     *  every day
     *  08:00 AM -> 10:00 PM -> 1 Euro
     *  10:00 PM -> 08:00 AM -> 0.5 Euro
     */

    private val tariff1 = TimeBasedTariff(
        id = TariffId(1),
        billingInterval = null,
        rates = setOf(
            rate1,
            rate2
        ),
        timeSlots = listOf(
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.TUESDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),


            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.WEDNESDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.THURSDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.FRIDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.FRIDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.FRIDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.FRIDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SATURDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SATURDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SATURDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SATURDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SUNDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SUNDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SUNDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeBasedTariff.TimeSlot(
                from = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.SUNDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = TimeBasedTariff.TimeSlot.Time(
                    DayOfWeek.MONDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),
        ),
        timeZone = timezone,
        currency = Currency.getInstance("EUR")
    )


    init {
        register(tariff1)
    }

    private fun register(tariff: Tariff) {
        tariffMap[tariff.id] = tariff
    }
}