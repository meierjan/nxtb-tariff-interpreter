package wtf.meier.tariff.validator

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import wtf.meier.tariff.interpreter.model.Interval

import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.tariff.validator.exception.tariff.*
import java.util.*
import java.util.concurrent.TimeUnit

internal class SlotBasedTariffValidatorTest {

    private val startInterval = Interval(0, TimeUnit.SECONDS)
    private val basicFixedRate = FixedRate(
        id = RateId(1),
        currency = Currency.getInstance("EUR"),
        price = Price(3)
    )

    private val slotBasedTariff = SlotBasedTariff(
        id = TariffId(0),
        rates = setOf(basicFixedRate),
        currency = Currency.getInstance("EUR"),
        slots = setOf(
            SlotBasedTariff.Slot(
                start = startInterval,
                rate = RateId(1),
                end = null
            )
        ),
        billingInterval = null
    )

    @Test
    fun `SlotBasedTariff - invalid RateId`() {
        val invalidSlot = SlotBasedTariff.Slot(
            start = startInterval,
            end = null,
            rate = RateId(3)
        )
        val invalidTariff = slotBasedTariff.copy(
            slots = setOf(invalidSlot)
        )

        val exception = assertThrows<InvalidSlotException> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("can not find a rate with id") ?: false)
    }

    @Test
    fun `SlotBasedTariff - empty slots`() {
        val invalidTariff = slotBasedTariff.copy(slots = emptySet())

        val exception = assertThrows<InvalidSlotBasedTariff> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("slots is empty") ?: false)
    }

    @Test
    fun `SlotBasedTariff - slots have no end value`() {
        val invalidTariff = slotBasedTariff.copy(
            slots = setOf(
                SlotBasedTariff.Slot(
                    start = startInterval,
                    end = null,
                    rate = RateId(1)
                ),
                SlotBasedTariff.Slot(
                    start = Interval(10, TimeUnit.MINUTES),
                    end = null,
                    rate = RateId(1)
                )
            )
        )

        val exception = assertThrows<InvalidSlotException> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("slot is not the last but the end value is null") ?: false)
    }

    @Test
    fun `last slot has null value for end`() {
        val invalidTariff = slotBasedTariff.copy(
            slots = setOf(
                SlotBasedTariff.Slot(
                    start = startInterval,
                    end = null,
                    rate = RateId(1)
                )
            )
        )

        invalidTariff.accept(Validator)
    }

    @Test
    fun `SlotBasedTariff - unconnected slots`() {
        val invalidTariff = slotBasedTariff.copy(
            slots = setOf(
                SlotBasedTariff.Slot(
                    start = startInterval,
                    end = Interval(10, TimeUnit.MINUTES),
                    rate = RateId(1)
                ),
                SlotBasedTariff.Slot(
                    start = Interval(20, TimeUnit.MINUTES),
                    end = null,
                    rate = RateId(1)
                )
            )
        )

        val exception = assertThrows<InvalidSlotBasedTariff> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("the slot with start: ") ?: false)
    }

    @Test
    fun `SlotBasedTariff - overlapped slot`() {
        val invalidTariff = slotBasedTariff.copy(
            slots = setOf(
                SlotBasedTariff.Slot(
                    start = startInterval,
                    end = Interval(10, TimeUnit.MINUTES),
                    rate = RateId(1)
                ),
                SlotBasedTariff.Slot(
                    start = Interval(5, TimeUnit.MINUTES),
                    end = null,
                    rate = RateId(1)
                )
            )
        )
        val exception = assertThrows<InvalidSlotBasedTariff> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("the slot with start: ") ?: false)
    }

    @Test
    fun `SlotBasedTariff - slots starts not at zero interval`() {
        val invalidTariff = slotBasedTariff.copy(
            slots = setOf(
                SlotBasedTariff.Slot(
                    start = Interval(5, TimeUnit.MINUTES),
                    end = null,
                    rate = RateId(1)
                )
            )
        )
        val exception = assertThrows<InvalidSlotBasedTariff> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("the slot with start: ") ?: false)
    }

    @Test
    fun `SlotBasedTariff - inconstant currencies`() {
        val invalidTariff = slotBasedTariff.copy(
            currency = Currency.getInstance("USD")
        )
        assertThrows<InconsistentCurrencyException> { invalidTariff.accept(Validator) }
    }
}

