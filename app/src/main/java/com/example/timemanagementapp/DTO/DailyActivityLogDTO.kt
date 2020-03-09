package com.example.timemanagementapp.DTO

import java.util.*

class DailyActivityLogDTO(
    private var date: Date,
    private var activityLog: List<ActivityDTO>
)