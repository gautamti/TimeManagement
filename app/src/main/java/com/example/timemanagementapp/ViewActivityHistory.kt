package com.example.timemanagementapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.timemanagementapp.DTO.ActivityDTO
import com.example.timemanagementapp.DTO.DailyActivityLogDTO
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.gson.Gson
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList


class ViewActivityHistory : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_history)
        drawChart()
    }


    fun switchToAddActivity(view: View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun drawChart(){
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
}
