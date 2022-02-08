package wtf.meier.tariff.interpreter.extension

import wtf.meier.tariff.interpreter.model.Price


fun minOf(a: Price, b: Price): Price = if (a.credit < b.credit) a else b

fun maxOf(a: Price, b: Price): Price = if (a.credit > b.credit) a else b

operator fun Price.minus(b: Price): Price = Price(this.credit - b.credit)