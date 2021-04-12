package wtf.meier.tariff.interpreter.model.tariff.calculator

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.HOURS

class SlotBasedTariffCalculatorTest {

    private lateinit var rateCalculator: RateCalculator
    private lateinit var calculator: SlotBasedTariffCalculator

    @BeforeEach
    fun setup() {
        rateCalculator = RateCalculator()
        calculator = SlotBasedTariffCalculator(rateCalculator)
    }

    /**
     * Slot based tariff if 2 slots
     * t < 20 min = 20,00 Euro (Fixed Rate)
     * t >= 20 min = 40,00 Euro (Fixed Rate)
     */
    private val slotBasedFixedRate1 = SlotBasedTariff(
        id = TariffId(1),
        freeSeconds = 0,
        rates = setOf(
            FixedRate(
                id = RateId(1),
                currency = Currency.getInstance("EUR"),
                price = Price(2000)
            )
        ),
        slots = setOf(
            SlotBasedTariff.Slot(
                start = Interval(0, TimeUnit.SECONDS),
                end = Interval(2, HOURS),
                rate = RateId(1)
            ),
            SlotBasedTariff.Slot(
                start = Interval(2, TimeUnit.HOURS),
                end = null,
                RateId(1)
            )
        ),
        billingInterval = Interval(1, TimeUnit.DAYS)
    )

    @Test
    fun `slotBasedFixedRate1 for 1 hours`() {

        val receipt = calculator.calculate(
            tariff = slotBasedFixedRate1,
            rentalStart = Instant.ofEpochMilli(0),
            rentalEnd = Instant.ofEpochMilli(HOURS.toMillis(1))
        )

        assert(receipt.price == 2000)
    }

    @Test
    fun `slotBasedFixedRate1 for exactly 2 - 1 milli hours`() {

        val receipt = calculator.calculate(
            tariff = slotBasedFixedRate1,
            rentalStart = Instant.ofEpochMilli(0),
            rentalEnd = Instant.ofEpochMilli(HOURS.toMillis(2))
        )

        assert(receipt.price == 2000)
    }

    @Test
    fun `slotBasedFixedRate1 for 4 hours`() {

        val receipt = calculator.calculate(
            tariff = slotBasedFixedRate1,
            rentalStart = Instant.ofEpochMilli(0),
            rentalEnd = Instant.ofEpochMilli(HOURS.toMillis(4))
        )

        println(receipt.price)
        assert(receipt.price == 4000)
    }

    @Test
    fun `slotBasedFixedRate1 for 10 hours`() {

        val receipt = calculator.calculate(
            tariff = slotBasedFixedRate1,
            rentalStart = Instant.ofEpochMilli(0),
            rentalEnd = Instant.ofEpochMilli(HOURS.toMillis(10))
        )

        assert(receipt.price == 4000)
    }
}