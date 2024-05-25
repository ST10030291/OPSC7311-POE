import java.text.SimpleDateFormat
import java.util.Date

data class DailyGoal(
    val userID: String? = null,
    val currentDate: String = SimpleDateFormat("yyyy-MM-dd").format(Date()),
    val minValue: Double? = null,
    val maxValue: Double? = null
)
