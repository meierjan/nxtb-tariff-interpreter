package wtf.meier.tariff.interpreter.model.tariff

import org.junit.jupiter.api.Test
import java.time.DayOfWeek

class TimeSlotTest {
    @Test
    fun `test two dates compare correct`() {
        val c1 = TimeBasedTariff.TimeSlot.Time(
                DayOfWeek.MONDAY, 14, 0
        )
        val c2 = TimeBasedTariff.TimeSlot.Time(
                DayOfWeek.MONDAY, 15, 0
        )

        assert(c1 < c2)
    }
}