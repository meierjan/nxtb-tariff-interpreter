package wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs.testData

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import wtf.meier.tariff.interpreter.Calculator
import wtf.meier.tariff.interpreter.ICalculator
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import wtf.meier.tariff.interpreter.model.tariff.TariffCalculator
import java.io.FileReader
import java.time.Instant
import java.util.concurrent.TimeUnit

@Serializable
data class DataPoint(val start: Long, val end: Long?, val price: Int)

@Serializable
data class PriceChart(val chart: Array<DataPoint>)



class PriceChartTester(){
    fun testPriceChart(path: String, tariff: Tariff, calculator: Calculator) {

        val format = Json { ignoreUnknownKeys = true }
        val priceChart: PriceChart = format.decodeFromString<PriceChart>(
            FileReader(path).readText()
        )

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


