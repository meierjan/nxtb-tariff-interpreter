package wtf.meier.tariff.interpreter

import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import java.util.*

interface ICalculator {
    fun calculate(tariff: Tariff, start: Date, end: Date): Receipt
}

