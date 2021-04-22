package wtf.meier.tariff.interpreter.model.tariff

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.lessThan
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

        assertThat(c1, lessThan(c2))
    }
}