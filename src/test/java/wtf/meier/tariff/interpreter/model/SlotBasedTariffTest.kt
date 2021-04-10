package wtf.meier.tariff.interpreter.model

import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.SlotBasedTariff.Slot
import java.util.*
import java.util.concurrent.TimeUnit.*

internal class SlotBasedTariffTest {

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
                Price(2000)
            ),
            FixedRate(
                id = RateId(2),
                currency = Currency.getInstance("EUR"),
                Price(2000)
            )
        ),
        slots = setOf(
            Slot(
                start = Interval(0, SECONDS),
                end = Interval(2, HOURS),
                RateId(1)
            ),
            Slot(
                start = Interval(2, HOURS),
                end = null,
                RateId(1)
            )
        ),
        billingInterval = Interval(1, DAYS)
    )

    @Test
    fun `slotBasedFixedRate1 for 1 hours`() {

        val receipt = slotBasedFixedRate1.calculate(rentalStart = Date(0), rentalEnd = Date(HOURS.toMillis(1)))

        println(receipt.price)
        assert(receipt.price == 2000)
    }

    @Test
    fun `slotBasedFixedRate1 for exactly 2 hours`() {

        val receipt = slotBasedFixedRate1.calculate(rentalStart = Date(0), rentalEnd = Date(HOURS.toMillis(2)) )

        println(receipt.price)
        assert(receipt.price == 2000)
    }

    @Test
    fun `slotBasedFixedRate1 for 4 hours`() {

        val receipt = slotBasedFixedRate1.calculate(rentalStart = Date(0), rentalEnd = Date(HOURS.toMillis(4)))

        println(receipt.price)
        assert(receipt.price == 4000)
    }

    @Test
    fun `slotBasedFixedRate1 for 10 hours`() {

        val receipt = slotBasedFixedRate1.calculate(rentalStart = Date(0), rentalEnd = Date(HOURS.toMillis(10)))

        println(receipt.price)
        assert(receipt.price == 4000)
    }
}
