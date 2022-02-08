package wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs.testData

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import wtf.meier.tariff.interpreter.Calculator
import wtf.meier.tariff.interpreter.model.RentalPeriod
import wtf.meier.tariff.interpreter.serializer.TariffDeserializer
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import java.io.FileReader
import java.time.Instant
import java.util.concurrent.TimeUnit

@Serializable
data class DataPoint(val start: Long, val end: Long?, val price: Int)

@Serializable
data class ChartMeta(val fictive_lending_start_time: Long?)

@Serializable
data class PriceChart(val chart: Array<DataPoint>, val chart_meta: ChartMeta)


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
                rentalPeriod = RentalPeriod(
                    rentalStart = Instant.ofEpochMilli(
                        TimeUnit.SECONDS.toMillis(
                            priceChart.chart_meta?.fictive_lending_start_time ?: 0L + 7200
                        )
                    ),
                    rentalEnd = Instant.ofEpochMilli(
                        TimeUnit.SECONDS.toMillis(
                            (priceChart.chart_meta?.fictive_lending_start_time ?: 0) + 86399 + 7200
                        )
                    )
                )
            )

            assertThat(
                receipt.price, equalTo(dataPoint.price)
            )
        }
    }
}


