package wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs


import kotlinx.datetime.Instant
import wtf.meier.tariff.interpreter.Calculator
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs.testData.PriceChartTester
import wtf.meier.tariff.serializer.TariffDeserializer
import wtf.meier.testing.readFileContent
import wtf.meier.testing.toMillis
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.DurationUnit

class TopTariffsTest {

    @Test
    fun `test tariff 10 `() {
        val receipt = Calculator().calculate(
            tariff = TariffDeserializer.deserializeTariff(readFileContent("src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs//testTariff/tariff10.json")),
            rentalPeriod = RentalPeriod(
                Instant.fromEpochMilliseconds(DurationUnit.SECONDS.toMillis(0)),
                Instant.fromEpochMilliseconds(1000000)
            )
        )

        assertEquals(
            receipt.price, 0
        )

    }

    @Test
    fun `test tariff 43`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0043.json",
            tariffPath = "src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff43.json",
        )
    }

    @Test
    fun `test tariff 165`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0165.json",
            tariffPath = "src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff165.json",
        )
    }

    @Test
    fun `test tariff 213`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0213.json",
            tariffPath = "src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff213.json",
        )
    }

    @Test
    fun `test tariff 262`() {
        PriceChartTester.testPriceChart(
            // /Users/meierjan/git/nxtbtariffinterpreterkmp/shared/topTariffs/testTariff/tariff315.json
            priceChartPath = "src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0262.json",
            tariffPath = "src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff262.json",
        )
    }

    @Test
    fun `test tariff 279`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0279.json",
            tariffPath = "src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff279.json",
        )
    }

    @Test
    fun `test tariff 299`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0299.json",
            tariffPath = "src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff299.json",
        )
    }

    @Test
    fun `test tariff 315`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0315.json",
            tariffPath = "src/commonTest/kotlin/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff315.json",
        )
    }
}
