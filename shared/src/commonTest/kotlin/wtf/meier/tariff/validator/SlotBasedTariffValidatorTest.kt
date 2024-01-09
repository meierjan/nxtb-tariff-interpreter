package wtf.meier.tariff.validator

import wtf.meier.tariff.interpreter.model.Currency
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.tariff.validator.exception.tariff.InconsistentCurrencyException
import wtf.meier.tariff.validator.exception.tariff.InvalidSlotBasedTariff
import wtf.meier.tariff.validator.exception.tariff.InvalidSlotException
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

internal class SlotBasedTariffValidatorTest {

    private val startInterval = 0.seconds
    private val basicFixedRate = FixedRate(
        id = RateId(1),
        currency = Currency("EUR"),
        price = Price(3)
    )

    private val slotBasedTariff = SlotBasedTariff(
        id = TariffId(0),
        rates = setOf(basicFixedRate),
        currency = Currency("EUR"),
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

        val exception = assertFailsWith<InvalidSlotException> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("can not find a rate with id") ?: false)
    }

    @Test
    fun `SlotBasedTariff - empty slots`() {
        val invalidTariff = slotBasedTariff.copy(slots = emptySet())

        val exception = assertFailsWith<InvalidSlotBasedTariff> { invalidTariff.accept(Validator) }
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
                    start = 10.minutes,
                    end = null,
                    rate = RateId(1)
                )
            )
        )

        val exception = assertFailsWith<InvalidSlotException> { invalidTariff.accept(Validator) }
        assertTrue(
            exception.message?.contains("slot is not the last but the end value is null") ?: false
        )
    }

    @Test
    fun `SlotBasedTariff - last slot has null value for end`() {
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
                    end = 10.minutes,
                    rate = RateId(1)
                ),
                SlotBasedTariff.Slot(
                    start = 20.minutes,
                    end = null,
                    rate = RateId(1)
                )
            )
        )

        val exception = assertFailsWith<InvalidSlotBasedTariff> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("the slot with start: ") ?: false)
    }

    @Test
    fun `SlotBasedTariff - overlapped slot`() {
        val invalidTariff = slotBasedTariff.copy(
            slots = setOf(
                SlotBasedTariff.Slot(
                    start = startInterval,
                    end = 10.minutes,
                    rate = RateId(1)
                ),
                SlotBasedTariff.Slot(
                    start = 5.minutes,
                    end = null,
                    rate = RateId(1)
                )
            )
        )
        val exception = assertFailsWith<InvalidSlotBasedTariff> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("the slot with start: ") ?: false)
    }

    @Test
    fun `SlotBasedTariff - slots starts not at zero interval`() {
        val invalidTariff = slotBasedTariff.copy(
            slots = setOf(
                SlotBasedTariff.Slot(
                    start = 5.minutes,
                    end = null,
                    rate = RateId(1)
                )
            )
        )
        val exception = assertFailsWith<InvalidSlotBasedTariff> { invalidTariff.accept(Validator) }
        assertTrue(exception.message?.contains("the slot with start: ") ?: false)
    }

    @Test
    fun `SlotBasedTariff - inconstant currencies`() {
        val invalidTariff = slotBasedTariff.copy(
            currency = Currency("USD")
        )
        assertFailsWith<InconsistentCurrencyException> { invalidTariff.accept(Validator) }
    }
}

