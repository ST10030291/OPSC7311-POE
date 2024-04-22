import android.net.Uri
import java.sql.Time
import java.util.Date
object TaskList{
    val taskList = mutableListOf<Task>()
}
data class Task(
    val taskName: String,
    val description: String,
    val category: String,
    val taskDate : String,
    val startTime : String,
    val endTime : String,
    val picture : Uri
)

// Hard coded User details
// This will be added to the Fire base database in the next part for better security
/*object TaskData {
    val taskEntry = mutableListOf<Task>(

    )
}*/