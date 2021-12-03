package wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import wtf.meier.tariff.interpreter.Calculator
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.helper.serializer.TariffDeserializer
import wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs.testData.PriceChartTester
import java.io.FileReader
import java.time.Instant
import java.util.concurrent.TimeUnit

class topTariffsTest {

    @Test
    fun `test tariff 10 `() {
        val receipt = Calculator().calculate(
            tariff = TariffDeserializer.deserializeTariff(FileReader("src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs//testTariff/tariff10.json").readText()),
            rentalPeriod = RentalPeriod(
                Instant.ofEpochMilli(TimeUnit.SECONDS.toMillis(0)),Instant.ofEpochMilli(1000000)
            ))

                    assertThat (receipt.price, equalTo(0)
        )

    }

    @Test
    fun `test tariff 43`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0043.json",
            tariffPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff43.json",
        )
    }

    @Test
    fun `test tariff 47`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0047.json",
            tariffPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff47.json",
        )
    }

    @Test
    fun `test tariff 55`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0055.json",
            tariffPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff55.json",
        )
    }

    @Test
    fun `test tariff 78`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0078.json",
            tariffPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff78.json",
        )
    }

    @Test
    // todo priceChart has invalid price!
    fun `test tariff 92`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0092.json",
            tariffPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff92.json",
        )
    }

    @Test
    fun `test tariff 165`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0165.json",
            tariffPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff165.json",
        )
    }

    @Test
    fun `test tariff 213`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0213.json",
            tariffPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff213.json",
        )
    }

    @Test
    fun `test tariff 262`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0262.json",
            tariffPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff262.json",
        )
    }

    @Test
    fun `test tariff 279`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0279.json",
            tariffPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff279.json",
        )
    }

    @Test
    fun `test tariff 299`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0299.json",
            tariffPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff299.json",
        )
    }

    @Test
    fun `test tariff 315`() {
        PriceChartTester.testPriceChart(
            priceChartPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testData/Rate0315.json",
            tariffPath = "src/test/java/wtf/meier/tariff/interpreter/model/tariff/calculator/topTariffs/testTariff/tariff315.json",
        )
    }
}
