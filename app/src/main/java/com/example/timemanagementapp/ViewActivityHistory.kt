package com.example.timemanagementapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.timemanagementapp.DTO.ActivityDTO
import com.example.timemanagementapp.DTO.ActivityGoalDTO
import com.example.timemanagementapp.DTO.ActivityGoalList
import com.example.timemanagementapp.DTO.DailyActivityLogDTO
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_view_history.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList


class ViewActivityHistory : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_history)
        drawChart()
        compareToGoals()
    }


    fun switchToAddActivity(view: View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun drawChart(){
        var appFilePath = filesDir
        val sdf = SimpleDateFormat("dd.M.yyyy")
        val currentDate = sdf.format(Date())
        val currentDateDirectoryName = "$appFilePath/$currentDate"
        val currentDateFile = File(currentDateDirectoryName)
        val gson = Gson()
        val activityLogFilePath = File("$currentDateFile/$currentDate.json")

        // load activity log for the day
        var jsonString = File(activityLogFilePath.toString()).readText()
        var dailyActivityLog: DailyActivityLogDTO = gson.fromJson(jsonString , DailyActivityLogDTO::class.java)
        val syncedActivityLog =  CopyOnWriteArrayList(dailyActivityLog.activityLog)

        //remove activities with a duration of zero
        for(activity in syncedActivityLog){
           if(activity.activityDuration ==0){
               syncedActivityLog.remove(activity)
           }
        }

        val pieChart: PieChart = findViewById(R.id.pieChart)
        pieChart.setUsePercentValues(true)

        val yvalues = ArrayList<PieEntry>()
        for (activity in syncedActivityLog){
            yvalues.add(PieEntry(activity.activityDuration.toFloat(), activity.activityName, 0))
        }

        val dataSet = PieDataSet(yvalues,"")
        val data = PieData(dataSet)

        data.setValueFormatter(PercentFormatter())
        pieChart.data = data
        pieChart.isDrawHoleEnabled = true
        pieChart.transparentCircleRadius = 58f
        pieChart.holeRadius = 58f
        dataSet.setColors(*ColorTemplate.PASTEL_COLORS)
        data.setValueTextSize(13f)
        data.setValueTextColor(Color.DKGRAY)
    }

    fun compareToGoals(){
        var appFilePath = filesDir
        val goalActivityListFileName = "$appFilePath/ActivityGoals"
        val goalActivityListFile = File(goalActivityListFileName)
        val gson = Gson()

        // load goals
        val goalListFilePath = File("$goalActivityListFile/goals.json")
        var jsonStringGoals = File(goalListFilePath.toString()).readText()
        val activityGoalList: ActivityGoalList = gson.fromJson(jsonStringGoals,ActivityGoalList::class.java)

        // create list of goal names
        val goalNames = CopyOnWriteArrayList<String>();
        for(goal in activityGoalList.activityGoals){
           goalNames.add(goal.activityName)
        }


        // load activity log for the day
        val sdf = SimpleDateFormat("dd.M.yyyy")
        val currentDate = sdf.format(Date())
        val currentDateDirectoryName = "$appFilePath/$currentDate"
        val currentDateFile = File(currentDateDirectoryName)
        val activityLogFilePath = File("$currentDateFile/$currentDate.json")
        var jsonStringActivities = File(activityLogFilePath.toString()).readText()
        var dailyActivityLog: DailyActivityLogDTO = gson.fromJson(jsonStringActivities , DailyActivityLogDTO::class.java)
        val syncedActivityLog =  CopyOnWriteArrayList(dailyActivityLog.activityLog)

        //remove activities with a duration of zero
        for(activity in syncedActivityLog){
            if(activity.activityDuration ==0){
                syncedActivityLog.remove(activity)
            }
        }

        var textToBeDisplayed = "Goal Status: "
        for(activity in syncedActivityLog){

            if(goalNames.contains(activity.activityName)){
                var indexOfgoal = goalNames.indexOf(activity.activityName)
                var goalToCompare: ActivityGoalDTO = activityGoalList.activityGoals.get(indexOfgoal)
                var activityToCompare: ActivityDTO = syncedActivityLog.get(indexOfgoal)
                if(activityToCompare.activityDuration > goalToCompare.activityDurationMax){
                    textToBeDisplayed =textToBeDisplayed + "\n" + "Activity " + activityToCompare.activityName + " " + " is above goal amount"
                }

                if(activityToCompare.activityDuration < goalToCompare.activityDurationMin){
                    textToBeDisplayed =textToBeDisplayed + "\n" + "Activity " + activityToCompare.activityName + " " + " is below goal amount"

                }
            }
        }
        goalStatus.setText(textToBeDisplayed)
    }
}
