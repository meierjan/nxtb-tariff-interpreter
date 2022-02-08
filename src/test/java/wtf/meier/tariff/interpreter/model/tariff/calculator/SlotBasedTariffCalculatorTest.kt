package wtf.meier.tariff.interpreter.model.tariff.calculator

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.model.Interval
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import wtf.meier.tariff.interpreter.model.tariff.BillingInterval
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.HOURS
import java.util.concurrent.TimeUnit.MINUTES

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
        currency = Currency.getInstance("EUR"),
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
                start = Interval(2, HOURS),
                end = null,
                RateId(1)
            )
        ),
        billingInterval = BillingInterval(duration = Interval(1, TimeUnit.DAYS))
    )

    @Test
    fun `slotBasedFixedRate1 for 1 hours`() {

        val receipt = calculator.calculate(
            tariff = slotBasedFixedRate1,
            rentalPeriod = RentalPeriod(
                Instant.ofEpochMilli(0), Instant.ofEpochMilli(HOURS.toMillis(1)),
            )
        )

        assertThat(receipt.price, equalTo(2000))
    }

    @Test
    fun `slotBasedFixedRate1 for exactly 2 - 1 milli hours`() {

        val receipt = calculator.calculate(
            tariff = slotBasedFixedRate1,
            rentalPeriod = RentalPeriod(Instant.ofEpochMilli(0), Instant.ofEpochMilli(HOURS.toMillis(2)))
        )

        assertThat(receipt.price, equalTo(2000))
    }

    @Test
    fun `slotBasedFixedRate1 for 4 hours`() {

        val receipt = calculator.calculate(
            tariff = slotBasedFixedRate1,
            rentalPeriod = RentalPeriod(
                Instant.ofEpochMilli(0), Instant.ofEpochMilli(HOURS.toMillis(4))
            )
        )

        assertThat(receipt.price, equalTo(4000))
    }

    @Test
    fun `slotBasedFixedRate1 for 10 hours`() {

        val receipt = calculator.calculate(
            tariff = slotBasedFixedRate1,
            rentalPeriod = RentalPeriod(
                Instant.ofEpochMilli(0), Instant.ofEpochMilli(HOURS.toMillis(10))
            )
        )

        assertThat(receipt.price, equalTo(4000))
    }


    private val leipzigBasisTariff = SlotBasedTariff(
        id = TariffId(2),
        currency = Currency.getInstance("EUR"),
        rates = setOf(
            TimeBasedRate(
                id = RateId(1),
                currency = Currency.getInstance("EUR"),
                interval = Interval(15, MINUTES),
                basePrice = Price(0),
                maxPrice = Price(1500),
                minPrice = Price(0),
                pricePerInterval = Price(100)
            )
        ),
        slots = setOf(
            SlotBasedTariff.Slot(
                start = Interval(0, HOURS),
                end = null,
                RateId(1)
            )
        ),
        billingInterval = BillingInterval(Interval(1, TimeUnit.DAYS))
    )

    @Test
    fun `leipzigBasis for 32 min`() {

        val receipt = calculator.calculate(
            tariff = leipzigBasisTariff,
            rentalPeriod = RentalPeriod(
                Instant.ofEpochMilli(0), Instant.ofEpochMilli(MINUTES.toMillis(32))
            )
        )

        assertThat(receipt.price, equalTo(300))
    }

    @Test
    fun `leipzigBasis for 26h`() {

        val receipt = calculator.calculate(
            tariff = leipzigBasisTariff,
            rentalPeriod = RentalPeriod(
                Instant.ofEpochMilli(0), Instant.ofEpochMilli(HOURS.toMillis(26))
            )
        )

        assertThat(receipt.price, equalTo(2300)) // 1500 for 24h and 2x400
    }

    @Test
    fun `leipzigBasis for 24h 17min`() {

        val receipt = calculator.calculate(
            tariff = leipzigBasisTariff,
            rentalPeriod = RentalPeriod(
                Instant.ofEpochMilli(0), Instant.ofEpochMilli(
                    HOURS.toMillis(24) + MINUTES.toMillis(17)
                )
            )
        )

        assertThat(receipt.price, equalTo(1700)) // 1500 for 24h and 2x100 for 17 minutes
    }


    private val jansBadTariff = SlotBasedTariff(
        id = TariffId(2),
        currency = Currency.getInstance("EUR"),
        rates = setOf(
            FixedRate(
                id = RateId(1),
                currency = Currency.getInstance("EUR"),
                price = Price(100)
            ),
        ),
        slots = setOf(
            SlotBasedTariff.Slot(
                start = Interval(0, MINUTES),
                end = Interval(17, MINUTES),
                RateId(1)
            ),
            SlotBasedTariff.Slot(
                start = Interval(17, MINUTES),
                end = Interval(34, MINUTES),
                RateId(1)
            ),
            SlotBasedTariff.Slot(
                start = Interval(34, MINUTES),
                end = null,
                RateId(1)
            )
        ),
        billingInterval = BillingInterval(Interval(20, MINUTES))
    )

    @Test
    fun `jansBadTariff for 35min`() {

        val receipt = calculator.calculate(
            tariff = jansBadTariff,
            rentalPeriod = RentalPeriod(
                Instant.ofEpochMilli(0), Instant.ofEpochMilli(MINUTES.toMillis(35))
            )
        )

        assertThat(receipt.price, equalTo(300))
    }

    @Test
    fun `jansBadTariff for 16min`() {

        val receipt = calculator.calculate(
            tariff = jansBadTariff,
            rentalPeriod = RentalPeriod(
                Instant.ofEpochMilli(0), Instant.ofEpochMilli(MINUTES.toMillis(16))
            )
        )

        assertThat(receipt.price, equalTo(100))
    }

    @Test
    fun `jansBadTariff for 19min`() {

        val receipt = calculator.calculate(
            tariff = jansBadTariff,
            rentalPeriod = RentalPeriod(
                Instant.ofEpochMilli(0), Instant.ofEpochMilli(
                    MINUTES.toMillis(19)
                )
            )
        )

        assertThat(receipt.price, equalTo(200))
    }

    @Test
    fun `jansBadTariff for 21min`() {

        val receipt = calculator.calculate(
            tariff = jansBadTariff,
            rentalPeriod = RentalPeriod(
                Instant.ofEpochMilli(0), Instant.ofEpochMilli(MINUTES.toMillis(21))
            )
        )

        assertThat(receipt.price, equalTo(300))
    }

    @Test
    fun `jansBadTariff for 36min`() {

        val receipt = calculator.calculate(
            tariff = jansBadTariff,
            rentalPeriod = RentalPeriod(
                Instant.ofEpochMilli(0), Instant.ofEpochMilli(MINUTES.toMillis(36))
            )
        )

        assertThat(receipt.price, equalTo(300))
    }

    @Test
    fun `jansBadTariff for 39min`() {

        val receipt = calculator.calculate(
            tariff = jansBadTariff,
            rentalPeriod = RentalPeriod(
                Instant.ofEpochMilli(0), Instant.ofEpochMilli(
                    MINUTES.toMillis(39)
                )
            )
        )

        assertThat(
            receipt.price, equalTo(400)
        )
    }

    @Test
    fun `jansBadTariff for 41min`() {

        val receipt = calculator.calculate(
            tariff = jansBadTariff,
            rentalPeriod = RentalPeriod(
                Instant.ofEpochMilli(0), Instant.ofEpochMilli(
                    MINUTES.toMillis(41)
                )
            )
        )

        assertThat(receipt.price, equalTo(500))
    }
}