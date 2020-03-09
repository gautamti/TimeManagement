package com.example.timemanagementapp.dto

import java.util.*

data class DailyActivityLogDTO(
    var date: Date,
    var activityLog: List<ActivityDTO>
)