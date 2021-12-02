package wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs.testData

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import wtf.meier.tariff.interpreter.Calculator
import wtf.meier.tariff.interpreter.helper.serializer.TariffDeserializer
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import java.io.FileReader
import java.time.Instant
import java.util.concurrent.TimeUnit

@Serializable
data class DataPoint(val start: Long, val end: Long?, val price: Int)

@Serializable
data class PriceChart(val chart: Array<DataPoint>)


object PriceChartTester {

    private val format = Json { ignoreUnknownKeys = true }
    private val calculator = Calculator()

    fun testPriceChart(priceChartPath: String, tariffPath: String) {

        val priceChart: PriceChart = format.decodeFromString(FileReader(priceChartPath).readText())
        val tariff: Tariff = TariffDeserializer.deserializeTariff(FileReader(tariffPath).readText())

        priceChart.chart.map { dataPoint ->
            if (dataPoint.end == null) return
            val receipt = calculator.calculate(
                tariff = tariff,
                start = Instant.ofEpochMilli(TimeUnit.SECONDS.toMillis(0)),
                end = Instant.ofEpochMilli(TimeUnit.SECONDS.toMillis(dataPoint.end))
            )

            assertThat(receipt.price, equalTo(dataPoint.price))
        }
    }
}


