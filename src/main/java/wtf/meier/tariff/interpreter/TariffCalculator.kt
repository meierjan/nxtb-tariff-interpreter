package wtf.meier.tariff.interpreter

import wtf.meier.tariff.interpreter.model.Tariff
import java.util.*


interface Calculator {
    fun calculate(tariff: Tariff, start: Date, end: Date): Receipt
}

data class Receipt(
    val price: Int,
    val currency: Currency,
    val log: List<String> = emptyList()
)

class TariffCalculator : Calculator {

    override fun calculate(tariff: Tariff, start: Date, end: Date): Receipt =
         tariff.calculate(start, end)

}