package wtf.meier.tariff.api.service.calculation

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import reactor.test.StepVerifier
import wtf.meier.tariff.api.repository.MockTariffRepository
import wtf.meier.tariff.api.repository.exception.TariffNotFoundException
import wtf.meier.tariff.api.service.tariff.TariffService
import wtf.meier.tariff.interpreter.Calculator
import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.tariff.TariffId
import java.time.Instant
import java.util.*


internal class RateCalculationServiceTest {

    private val mockedCalculator = mockk<Calculator>()
    private val rateCalculationService: IRateCalculationService = RateCalculationService(tariffService = TariffService(MockTariffRepository()), tariffCalculator = mockedCalculator)

    private val receipt = Receipt(
        positions = listOf(
            Receipt.Position(
                description = "test", positionStart = Instant.ofEpochSecond(1), positionEnd = Instant.ofEpochSecond(100)
            )
        ), currency = Currency.getInstance("EUR")
    )

    @Test
    fun `calculate Tariff 1`() {
          every { mockedCalculator.calculate(any(), any()) } returns receipt

        val result = rateCalculationService.calculateRate(
            tariffId = TariffId(1), from = Instant.ofEpochMilli(0), to = Instant.ofEpochSecond(100)
        )
        StepVerifier.create(result).expectNext(receipt).verifyComplete()
    }

    @Test fun `calculate nonexistent tariff`(){
        val result = rateCalculationService.calculateRate(tariffId = TariffId(999), Instant.ofEpochSecond(1), Instant.ofEpochSecond(100))
        StepVerifier.create(result).verifyError(TariffNotFoundException::class.java)
    }
}