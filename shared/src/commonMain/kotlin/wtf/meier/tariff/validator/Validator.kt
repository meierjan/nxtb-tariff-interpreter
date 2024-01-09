package wtf.meier.tariff.validator

import kotlinx.datetime.Clock
import wtf.meier.tariff.interpreter.IVisitor
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.DynamicGoodwill
import wtf.meier.tariff.interpreter.model.goodwill.FreeMinutes
import wtf.meier.tariff.interpreter.model.goodwill.StaticGoodwill
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.validator.exception.InvalidRentalPeriodException
import wtf.meier.tariff.validator.exception.tariff.InconsistentCurrencyException
import wtf.meier.tariff.validator.exception.tariff.InvalidDynamicGoodwillException
import wtf.meier.tariff.validator.exception.tariff.InvalidIntervalException
import wtf.meier.tariff.validator.exception.tariff.InvalidPriceException
import wtf.meier.tariff.validator.exception.tariff.InvalidRateIdException
import wtf.meier.tariff.validator.exception.tariff.InvalidSlotBasedTariff
import wtf.meier.tariff.validator.exception.tariff.InvalidSlotException
import kotlin.time.Duration.Companion.seconds

object Validator : IVisitor {

    override fun visitSlotBasedTariff(slotBasedTariff: SlotBasedTariff) {
        slotBasedTariff.rates.forEach { it.accept(this) }
        slotBasedTariff.slots.forEach { it.accept(this) }
        slotBasedTariff.billingInterval?.accept(this)
        slotBasedTariff.goodwill?.accept(this)

        val rateMap = slotBasedTariff.rates.associateBy { it.id }

        // CHECK: slots is not empty
        if (slotBasedTariff.slots.isEmpty()) throw InvalidSlotBasedTariff("slots is empty")

        val sortedSlots = slotBasedTariff.slots.sortedBy { it.start }
        var actualInterval: Interval? = 0.seconds

        sortedSlots.forEach {
            // CHECK: all slots has an end value excepted the last one
            if (it != sortedSlots.last() && it.end == null) throw InvalidSlotException("slot is not the last but the end value is null")
            // CHECK: all slots are connected and not overlapped
            if (actualInterval != it.start) throw InvalidSlotBasedTariff("the slot with start: ${it.start} and end: ${it.end} is not connected to the slot before")
            else {
                actualInterval = it.end
            }

            // CHECK: is there an existing rate
            if (rateMap[it.rate] == null) throw InvalidSlotException("can not find a rate with id ${it.rate}")
        }

        // CHECK: uniqueness of rate id's and the currency consistency of rates and the tariff
        checkRates(slotBasedTariff)

        // CHECK: the last slot has null as end value
        if (actualInterval != null) throw InvalidSlotException("the end value of the last slot is not null")
    }


    private fun checkRates(tariff: Tariff) {
        val rates = tariff.rates
        val rateIds = mutableListOf<RateId>()
        rates.forEach {
            // CHECK: has the slot same currency like the tariff
            if (it.currency != tariff.currency) throw InconsistentCurrencyException("slot currency: ${it.currency}, tariff currency: ${tariff.currency}")
            rateIds.add(it.id)
        }
        //CHECK: that all rate id's are unique
        if (rateIds.size != rateIds.distinct().size) throw InvalidRateIdException("all rate id`s must be unique")
    }

    override fun visitSlot(slot: SlotBasedTariff.Slot) {
        if ((slot.end != null) && (slot.start >= slot.end)) throw InvalidSlotException("slot end is before")

        slot.start.accept(this)
        slot.end?.accept(this)
    }

    override fun visitFixedRate(fixedRate: FixedRate) {
        fixedRate.price.accept(this)
    }

    override fun visitTimeBasedRate(timeBasedRate: TimeBasedRate) {
        timeBasedRate.interval.accept(this)
        timeBasedRate.basePrice.accept(this)
        timeBasedRate.pricePerInterval.accept(this)
        timeBasedRate.maxPrice.accept(this)
        timeBasedRate.minPrice.accept(this)
    }

    override fun visitStaticGoodwill(staticGoodwill: StaticGoodwill) {
        staticGoodwill.duration.accept(this)
    }

    override fun visitDynamicGoodwill(dynamicGoodwill: DynamicGoodwill) {
        if (dynamicGoodwill.deductibleProportionInPercentage !in 0.0..100.0) throw InvalidDynamicGoodwillException(
            "deductibleProportionInPercentage not in range: ${dynamicGoodwill.deductibleProportionInPercentage}"
        )
    }

    override fun visitFreeMinutes(freeMinutes: FreeMinutes) {
        freeMinutes.duration.accept(this)
    }


    override fun visitPrice(price: Price) {
        if (price.credit < 0) throw InvalidPriceException("The price is negative: ${price.credit}")
    }


    override fun visitRentalPeriod(rentalPeriod: RentalPeriod) {
        if (rentalPeriod.rentalStart >= rentalPeriod.rentalEnd) throw InvalidRentalPeriodException("rentalEnd is before rentalStart")
        if (rentalPeriod.rentalEnd > Clock.System.now()) throw InvalidRentalPeriodException("rental end is in future")
    }

    override fun visitDuration(duration: Interval) {
        if (duration.isNegative()) throw InvalidIntervalException("duration is negative: $duration")
    }

}