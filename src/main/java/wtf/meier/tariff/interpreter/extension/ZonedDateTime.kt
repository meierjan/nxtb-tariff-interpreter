package wtf.meier.tariff.interpreter.extension

import java.time.ZonedDateTime


fun min(t1: ZonedDateTime, t2: ZonedDateTime) =
        if (t1.isBefore(t2)) {
            t1
        } else {
            t2
        }

operator fun ZonedDateTime.minus(slotStart: ZonedDateTime): Long =
        this.toInstant().toEpochMilli() - slotStart.toInstant().toEpochMilli()
