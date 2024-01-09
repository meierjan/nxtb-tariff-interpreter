package wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs.testData

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import wtf.meier.tariff.interpreter.Calculator
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.serializer.TariffDeserializer
import wtf.meier.testing.readFileContent
import kotlin.test.assertEquals

@Serializable
data class DataPoint(val start: Long, val end: Long?, val price: Int)

@Serializable
data class PriceChart(val chart: Array<DataPoint>)


object PriceChartTester {

    private val format = Json { ignoreUnknownKeys = true }
    private val calculator = Calculator()

    fun testPriceChart(priceChartPath: String, tariffPath: String) {

        val priceChart: PriceChart = format.decodeFromString(readFileContent(priceChartPath))
        val tariff: Tariff = TariffDeserializer.deserializeTariff(readFileContent(tariffPath))

        priceChart.chart.map { dataPoint ->
            if (dataPoint.end == null) return
            val receipt = calculator.calculate(
                tariff = tariff,
                rentalPeriod = RentalPeriod(
                    rentalEnd = Instant.fromEpochSeconds(dataPoint.end)
                )
            )

            assertEquals(
                receipt.price, dataPoint.price
            )
        }
    }
}


