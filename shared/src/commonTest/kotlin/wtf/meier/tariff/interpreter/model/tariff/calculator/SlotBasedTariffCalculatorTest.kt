package wtf.meier.tariff.interpreter.model.tariff.calculator

import kotlinx.datetime.Instant
import wtf.meier.tariff.interpreter.model.Currency
import wtf.meier.tariff.interpreter.model.Price
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.rate.FixedRate
import wtf.meier.tariff.interpreter.model.rate.RateCalculator
import wtf.meier.tariff.interpreter.model.rate.RateId
import wtf.meier.tariff.interpreter.model.rate.TimeBasedRate
import wtf.meier.tariff.interpreter.model.tariff.SlotBasedTariff
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import wtf.meier.testing.toMillis
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class SlotBasedTariffCalculatorTest {

    private lateinit var rateCalculator: RateCalculator
    private lateinit var calculator: SlotBasedTariffCalculator

    @BeforeTest
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
        currency = Currency("EUR"),
        rates = setOf(
            FixedRate(
                id = RateId(1),
                currency = Currency("EUR"),
                price = Price(2000)
            )
        ),
        slots = setOf(
            SlotBasedTariff.Slot(
                start = 0.seconds,
                end = 2.hours,
                rate = RateId(1)
            ),
            SlotBasedTariff.Slot(
                start = 2.hours,
                end = null,
                RateId(1)
            )
        ),
        billingInterval = 1.days
    )

    @Test
    fun `slotBasedFixedRate1 for 1 hours`() {

        val receipt = calculator.calculate(
            tariff = slotBasedFixedRate1,
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(0),
                Instant.fromEpochMilliseconds(DurationUnit.HOURS.toMillis(1)),
            )
        )

        assertEquals(receipt.price, 2000)
    }

    @Test
    fun `slotBasedFixedRate1 for exactly 2 - 1 milli hours`() {

        val receipt = calculator.calculate(
            tariff = slotBasedFixedRate1,
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(0),
                Instant.fromEpochMilliseconds(DurationUnit.HOURS.toMillis(2))
            )
        )

        assertEquals(receipt.price, 2000)
    }

    @Test
    fun `slotBasedFixedRate1 for 4 hours`() {

        val receipt = calculator.calculate(
            tariff = slotBasedFixedRate1,
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(0),
                Instant.fromEpochMilliseconds(DurationUnit.HOURS.toMillis(4))
            )
        )

        assertEquals(receipt.price, 4000)
    }

    @Test
    fun `slotBasedFixedRate1 for 10 hours`() {

        val receipt = calculator.calculate(
            tariff = slotBasedFixedRate1,
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(0),
                Instant.fromEpochMilliseconds(DurationUnit.HOURS.toMillis(10))
            )
        )

        assertEquals(receipt.price, 4000)
    }


    private val leipzigBasisTariff = SlotBasedTariff(
        id = TariffId(2),
        currency = Currency("EUR"),
        rates = setOf(
            TimeBasedRate(
                id = RateId(1),
                currency = Currency("EUR"),
                interval = 15.minutes,
                basePrice = Price(0),
                maxPrice = Price(1500),
                minPrice = Price(0),
                pricePerInterval = Price(100)
            )
        ),
        slots = setOf(
            SlotBasedTariff.Slot(
                start = 0.hours,
                end = null,
                RateId(1)
            )
        ),
        billingInterval = 1.days
    )

    @Test
    fun `leipzigBasis for 32 min`() {

        val receipt = calculator.calculate(
            tariff = leipzigBasisTariff,
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(0),
                Instant.fromEpochMilliseconds(DurationUnit.MINUTES.toMillis(32))
            )
        )

        assertEquals(receipt.price, 300)
    }

    @Test
    fun `leipzigBasis for 26h`() {

        val receipt = calculator.calculate(
            tariff = leipzigBasisTariff,
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(0),
                Instant.fromEpochMilliseconds(26.hours.inWholeMilliseconds)
            )
        )

        assertEquals(receipt.price, 2300) // 1500 for 24h and 2x400
    }

    @Test
    fun `leipzigBasis for 24h 17min`() {

        val receipt = calculator.calculate(
            tariff = leipzigBasisTariff,
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(0), Instant.fromEpochMilliseconds(
                    DurationUnit.HOURS.toMillis(24) + DurationUnit.MINUTES.toMillis(17)
                )
            )
        )

        assertEquals(receipt.price, 1700) // 1500 for 24h and 2x100 for 17 minutes
    }


    private val jansBadTariff = SlotBasedTariff(
        id = TariffId(2),
        currency = Currency("EUR"),
        rates = setOf(
            FixedRate(
                id = RateId(1),
                currency = Currency("EUR"),
                price = Price(100)
            ),
        ),
        slots = setOf(
            SlotBasedTariff.Slot(
                start = 0.minutes,
                end = 17.minutes,
                RateId(1)
            ),
            SlotBasedTariff.Slot(
                start = 17.minutes,
                end = 34.minutes,
                RateId(1)
            ),
            SlotBasedTariff.Slot(
                start = 34.minutes,
                end = null,
                RateId(1)
            )
        ),
        billingInterval = 20.minutes
    )

    @Test
    fun `jansBadTariff for 35min`() {

        val receipt = calculator.calculate(
            tariff = jansBadTariff,
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(0),
                Instant.fromEpochMilliseconds(DurationUnit.MINUTES.toMillis(35))
            )
        )

        assertEquals(receipt.price, 300)
    }

    @Test
    fun `jansBadTariff for 16min`() {

        val receipt = calculator.calculate(
            tariff = jansBadTariff,
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(0),
                Instant.fromEpochMilliseconds(DurationUnit.MINUTES.toMillis(16))
            )
        )

        assertEquals(receipt.price, 100)
    }

    @Test
    fun `jansBadTariff for 19min`() {

        val receipt = calculator.calculate(
            tariff = jansBadTariff,
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(0), Instant.fromEpochMilliseconds(
                    DurationUnit.MINUTES.toMillis(19)
                )
            )
        )

        assertEquals(receipt.price, 200)
    }

    @Test
    fun `jansBadTariff for 21min`() {

        val receipt = calculator.calculate(
            tariff = jansBadTariff,
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(0),
                Instant.fromEpochMilliseconds(DurationUnit.MINUTES.toMillis(21))
            )
        )

        assertEquals(receipt.price, 300)
    }

    @Test
    fun `jansBadTariff for 36min`() {

        val receipt = calculator.calculate(
            tariff = jansBadTariff,
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(0),
                Instant.fromEpochMilliseconds(DurationUnit.MINUTES.toMillis(36))
            )
        )

        assertEquals(receipt.price, 300)
    }

    @Test
    fun `jansBadTariff for 39min`() {

        val receipt = calculator.calculate(
            tariff = jansBadTariff,
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(0), Instant.fromEpochMilliseconds(
                    DurationUnit.MINUTES.toMillis(39)
                )
            )
        )

        assertEquals(
            receipt.price, 400
        )
    }

    @Test
    fun `jansBadTariff for 41min`() {

        val receipt = calculator.calculate(
            tariff = jansBadTariff,
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(0), Instant.fromEpochMilliseconds(
                    DurationUnit.MINUTES.toMillis(41)
                )
            )
        )

        assertEquals(receipt.price, 500)
    }
}