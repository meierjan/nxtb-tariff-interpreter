package wtf.meier.tariff.api.service.tariff

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff.TimeSlot
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff.TimeSlot.Time
import java.time.DayOfWeek.FRIDAY
import java.time.DayOfWeek.MONDAY
import java.time.DayOfWeek.SATURDAY
import java.time.DayOfWeek.SUNDAY
import java.time.DayOfWeek.THURSDAY
import java.time.DayOfWeek.TUESDAY
import java.time.DayOfWeek.WEDNESDAY
import java.time.ZoneId
import java.util.*

class TariffNotFoundException(tariffId: TariffId) :
    RuntimeException("tariff with id $tariffId wasn't found")

class TariffAlreadyExistsException(tariffId: TariffId) :
    RuntimeException("tariff with id $tariffId already exists")

class MockTariffServiceImpl : TariffService {


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
            TimeSlot(
                from = Time(
                    MONDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = Time(
                    MONDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeSlot(
                from = Time(
                    MONDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = Time(
                    TUESDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeSlot(
                from = Time(
                    TUESDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = Time(
                    TUESDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeSlot(
                from = Time(
                    TUESDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = Time(
                    WEDNESDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),


            TimeSlot(
                from = Time(
                    WEDNESDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = Time(
                    WEDNESDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeSlot(
                from = Time(
                    WEDNESDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = Time(
                    THURSDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeSlot(
                from = Time(
                    THURSDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = Time(
                    THURSDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeSlot(
                from = Time(
                    THURSDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = Time(
                    FRIDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeSlot(
                from = Time(
                    FRIDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = Time(
                    FRIDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeSlot(
                from = Time(
                    FRIDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = Time(
                    SATURDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeSlot(
                from = Time(
                    SATURDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = Time(
                    SATURDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeSlot(
                from = Time(
                    SATURDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = Time(
                    SUNDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),

            TimeSlot(
                from = Time(
                    SUNDAY,
                    hour = 8,
                    minutes = 0
                ),
                to = Time(
                    SUNDAY,
                    hour = 22,
                    minutes = 0
                ),
                rate = rate1.id
            ),
            TimeSlot(
                from = Time(
                    SUNDAY,
                    hour = 22,
                    minutes = 0
                ),
                to = Time(
                    MONDAY,
                    hour = 8,
                    minutes = 0
                ),
                rate = rate2.id
            ),
        ),
        timeZone = timezone,
        currency = Currency.getInstance("EUR")
    )

    private val tariffMap: MutableMap<TariffId, Tariff> = mutableMapOf()

    init {
        register(tariff1)
    }

    private fun register(tariff: Tariff) {
        tariffMap[tariff.id] = tariff
    }

    override fun getTariff(tariffId: TariffId): Mono<Tariff> =
        Mono.fromCallable { tariffMap[tariffId] ?: throw TariffNotFoundException(tariffId) }

    override fun getAllTariffs(): Flux<Tariff> {
        return Mono.just(tariffMap.values).flatMapMany { Flux.fromIterable(it) }
    }

    override fun createTariff(tariff: Tariff): Mono<Tariff> {
        if (tariffMap[tariff.id] != null) throw TariffAlreadyExistsException(tariff.id)
        tariffMap[tariff.id] = tariff
        return Mono.just(tariff)
    }

    override fun setTariff(tariff: Tariff): Mono<Tariff> {
        if (tariffMap[tariff.id] == null) throw TariffNotFoundException(tariff.id)
        tariffMap[tariff.id] = tariff
        return Mono.just(tariff)
    }

    override fun deleteTariff(tariffId : TariffId): Mono<TariffId>{
        return Mono.just(tariffMap.remove(tariffId)?.id)
    }


}