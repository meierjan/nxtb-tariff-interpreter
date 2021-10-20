package wtf.meier.tariff.interpreter.model.tariff.calculator.topTariffs.testData

data class DataPoint(val start: Long, val end: Long, val price: Int)
data class PriceChart(val chart: Array<DataPoint>)
