package wtf.meier.tariff.interpreter

import wtf.meier.tariff.interpreter.model.Receipt
import wtf.meier.tariff.interpreter.model.tariff.Tariff
import java.time.Instant

interface ICalculator {
    fun calculate(tariff: Tariff, start: Instant, end: Instant): Receipt
}

