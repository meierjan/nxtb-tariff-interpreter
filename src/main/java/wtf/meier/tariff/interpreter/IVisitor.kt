package wtf.meier.tariff.interpreter

import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.goodwill.DynamicGoodwill
import wtf.meier.tariff.interpreter.model.goodwill.FreeMinutes
import wtf.meier.tariff.interpreter.model.goodwill.StaticGoodwill
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TimeBasedTariff

interface IVisitor {
    fun visitSlotBasedTariff(slotBasedTariff: SlotBasedTariff)
    fun visitTimeBasedTariff(timeBasedTariff: TimeBasedTariff)

    fun visitSlot(slot: SlotBasedTariff.Slot)
    fun visitTimeSlot(timeSlot: TimeBasedTariff.TimeSlot)

    fun visitFixedRate(fixedRate: FixedRate)
    fun visitTimeBasedRate(timeBasedRate: TimeBasedRate)

    fun visitStaticGoodwill(staticGoodwill: StaticGoodwill)
    fun visitDynamicGoodwill(dynamicGoodwill: DynamicGoodwill)
    fun visitFreeMinutes(freeMinutes: FreeMinutes)


    fun visitInterval(interval: Interval)
    fun visitPrice(price: Price)
    fun visitTime(time: TimeBasedTariff.TimeSlot.Time)

    fun visitRentalPeriod(rentalPeriod: RentalPeriod)

}