package wtf.meier.tariff.interpreter.extension

import wtf.meier.tariff.interpreter.model.Interval
import java.util.*


fun min(d1: Date, d2: Date) =
    if (d1.before(d2)) d1 else d2

operator fun Date.plus(interval: Interval): Date =
    Date(time + interval.durationMillis())