package wtf.meier.tariff.validator

import wtf.meier.tariff.interpreter.IVisitor
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.DynamicGoodwill
import wtf.meier.tariff.interpreter.model.goodwill.FreeMinutes
import wtf.meier.tariff.interpreter.model.goodwill.StaticGoodwill
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.extension.compareTo
import wtf.meier.tariff.interpreter.extension.durationMillis
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff
import wtf.meier.tariff.validator.exception.tariff.*
import java.util.concurrent.TimeUnit

object Validator : IVisitor {

    override fun visitSlotBasedTariff(slotBasedTariff: SlotBasedTariff) {
        val rateMap = slotBasedTariff.rates.associateBy { it.id }

        // CHECK: slots is not empty
        if (slotBasedTariff.slots.isEmpty()) throw InvalidSlotBasedTariff("slots is empty")

        val sortedSlots = slotBasedTariff.slots.sortedBy { it.start.durationMillis() }
        var actualInterval: Interval? = Interval(0, TimeUnit.SECONDS)

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

    override fun visitTimeBasedTariff(timeBasedTariff: TimeBasedTariff) {
        // CHECK: timeSlots is not empty
        if (timeBasedTariff.timeSlots.isEmpty()) throw InvalidTimeBasedTariffException("timeSlots is empty")

        // CHECK: uniqueness of rate id's and the currency consistency of rates and the tariff
        checkRates(timeBasedTariff)


        var dayCounter = 0
        var slotEnd: TimeBasedTariff.TimeSlot.Time? = null
        timeBasedTariff.timeSlots.forEach {
            // CHECK: all slots are connected and not overlapped
            if (slotEnd != null && slotEnd != it.from) throw InvalidTimeSlotException("the timeSlot with start value: ${it.from} and end value ${it.to} is not connected to the slot before")
            slotEnd = it.to
            it.from.day.value

            // accumulates day differences between "from" and "to" of a slot
            if (!(it.from.day == it.to.day && it.from < it.to)) {
                val daysBetween = (7 + it.to.day.value - it.from.day.value) % 7
                dayCounter += if (daysBetween != 0) daysBetween else 7
            }
        }

        // CHECK: is from and to from first and last slot identical?
        if (timeBasedTariff.timeSlots.first().from != timeBasedTariff.timeSlots.last().to) throw InvalidTimeSlotException(
            "slots covers not exactly a week"
        )

        // CHECK: Do the slots covers a whole week
        if (dayCounter != 7) throw InvalidTimeSlotException("slots covers not exactly a week")
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
        // visitor goes to start and end interval and checks them automatically

    }

    override fun visitTimeSlot(timeSlot: TimeBasedTariff.TimeSlot) {
        //visitor goes to from and to time automatically
    }

    override fun visitFixedRate(fixedRate: FixedRate) {
        // visitor goes to price and checks it automatically
    }

    override fun visitTimeBasedRate(timeBasedRate: TimeBasedRate) {
        // visitor goes to all prices and checks it automatically
    }

    override fun visitStaticGoodwill(staticGoodwill: StaticGoodwill) {
        // visitor goes to interval and checks it automatically
    }

    override fun visitDynamicGoodwill(dynamicGoodwill: DynamicGoodwill) {
        if (dynamicGoodwill.deductibleProportionInPercentage !in 0.0..100.0) throw InvalidDynamicGoodwillException("deductibleProportionInPercentage not in range: ${dynamicGoodwill.deductibleProportionInPercentage}")
    }

    override fun visitFreeMinutes(freeMinutes: FreeMinutes) {
        // visitor goes to interval and checks it automatically
    }

    override fun visitInterval(interval: Interval) {
        if (interval.timeAmount <= 0) throw InvalidIntervalException("The interval is negative: ${interval.timeAmount}")
    }

    override fun visitPrice(price: Price) {
        if (price.credit < 0) throw InvalidPriceException("The price is negative: ${price.credit}")
    }

    override fun visitTime(time: TimeBasedTariff.TimeSlot.Time) {
        if (time.hour !in 0..23) throw InvalidTimeException("hour is not between 0 and 24")
        if (time.minutes !in 0..59) throw InvalidTimeException("minutes is not between 0 and 60")
    }

    override fun visitRentalPeriod(rentalPeriod: RentalPeriod) {
    }

}