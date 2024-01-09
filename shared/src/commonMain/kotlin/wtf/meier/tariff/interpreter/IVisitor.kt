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


interface IVisitor {
    fun visitSlotBasedTariff(slotBasedTariff: SlotBasedTariff)

    fun visitSlot(slot: SlotBasedTariff.Slot)

    fun visitFixedRate(fixedRate: FixedRate)
    fun visitTimeBasedRate(timeBasedRate: TimeBasedRate)

    fun visitStaticGoodwill(staticGoodwill: StaticGoodwill)
    fun visitDynamicGoodwill(dynamicGoodwill: DynamicGoodwill)
    fun visitFreeMinutes(freeMinutes: FreeMinutes)


    fun visitPrice(price: Price)

    fun visitRentalPeriod(rentalPeriod: RentalPeriod)
    fun visitDuration(duration: Interval)

}