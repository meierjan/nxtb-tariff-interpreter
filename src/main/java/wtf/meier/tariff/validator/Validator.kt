package wtf.meier.tariff.validator

import wtf.meier.tariff.interpreter.IVisitor
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

object Validator : IVisitor {
    override fun visitSlotBasedTariff(slotBasedTariff: SlotBasedTariff) {
        TODO("Not yet implemented")
    }

    override fun visitTimeBasedTariff(timeBasedTariff: TimeBasedTariff) {
        TODO("Not yet implemented")
    }

    override fun visitSlot(slot: SlotBasedTariff.Slot) {
        TODO("Not yet implemented")
    }

    override fun visitTimeSlot(timeSlot: TimeBasedTariff.TimeSlot) {
        TODO("Not yet implemented")
    }

    override fun visitFixedRate(fixedRate: FixedRate) {
        TODO("Not yet implemented")
    }

    override fun visitTimeBasedRate(timeBasedRate: TimeBasedRate) {
        TODO("Not yet implemented")
    }

    override fun visitStaticGoodwill(staticGoodwill: StaticGoodwill) {
        TODO("Not yet implemented")
    }

    override fun visitDynamicGoodwill(dynamicGoodwill: DynamicGoodwill) {
        TODO("Not yet implemented")
    }

    override fun visitFreeMinutes(freeMinutes: FreeMinutes) {
        TODO("Not yet implemented")
    }

    override fun visitInterval(interval: Interval) {
        TODO("Not yet implemented")
    }

    override fun visitPrice(price: Price) {
        TODO("Not yet implemented")
    }

    override fun visitRentalPeriod(rentalPeriod: RentalPeriod) {
        TODO("Not yet implemented")
    }

}