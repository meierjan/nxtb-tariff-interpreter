package wtf.meier.tariff.interpreter.model

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

data class ZonedInstant(
    val instant: Instant,
    val zone: TimeZone
) {
    constructor(
        year: Int,
        monthNumber: Int,
        dayOfMonth: Int,
        hour: Int,
        minute: Int,
        second: Int,
        nanosecond: Int,
        zone: TimeZone
    ) :
            this(
                LocalDateTime(
                    year,
                    monthNumber,
                    dayOfMonth,
                    hour,
                    minute,
                    second,
                    nanosecond
                ).toInstant(zone), zone
            )

}