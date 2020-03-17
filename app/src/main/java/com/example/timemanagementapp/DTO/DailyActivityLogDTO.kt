package com.example.timemanagementapp.DTO

import java.util.*

class DailyActivityLogDTO(
    var date: String,
    var activityLog: MutableList<ActivityDTO>
)