class DailyGoal(
    var userID: String = "", // Assuming these are the properties of DailyGoal
    var currentDate: String = "",
    var minValue: Double = 0.0,
    var maxValue: Double = 0.0,
    var AppUsageTime: String = ""
) {
    // No-argument constructor
    constructor() : this("", "", 0.0, 0.0, "")
}

