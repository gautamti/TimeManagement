package com.example.timemanagementapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.timemanagementapp.DTO.ActivityDTO
import com.example.timemanagementapp.DTO.DailyActivityLogDTO
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    val DEFAULTACTIVITYNAME = "UC2020_DefaultActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        var appFilePath = filesDir
        val sdf = SimpleDateFormat("dd.M.yyyy")
        val currentDate = sdf.format(Date())
        val currentDateDirectoryName = "$appFilePath/$currentDate"
        val currentDateFile = File(currentDateDirectoryName)
        val gson = Gson()

        // if app root directory does not exist, make it
        if(!appFilePath.isDirectory){
            appFilePath.mkdir()
        }
        // if there is not a directory for the current day, make one
        if(!currentDateFile.isDirectory){
            currentDateFile.mkdir()
        }

        // if an activity log for the day has not been made, make a blank one and save it
        val activityLogFilePath = File("$currentDateFile/$currentDate.json")
        activityLogFilePath.setWritable(true)
        if(!activityLogFilePath.exists()){

            // Gson needs the file to not be null so a default activity log needs to be created
            var newActivityLogList = ArrayList<ActivityDTO>()
            var defaultActivity = ActivityDTO(DEFAULTACTIVITYNAME,0)
            newActivityLogList.add(defaultActivity)
            var dailyActivityLog = DailyActivityLogDTO(newActivityLogList,currentDate)

            // converts the object to a JSON string
            var jsonString = gson.toJson(dailyActivityLog)
            File(activityLogFilePath.toString()).writeText(jsonString)

        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

         fun addActivity(view: View) {
            val newActivityName = txtActivity.text
            val newActivityLength = txtTimeSpent.text
            var appFilePath = filesDir
            val sdf = SimpleDateFormat("dd.M.yyyy")
            val currentDate = sdf.format(Date()).toString()
            val currentDateDirectoryName = "$appFilePath/$currentDate"
            val currentDateFile = File(currentDateDirectoryName)
            val gson = Gson()

            val activityLogFilePath = File("$currentDateFile/$currentDate.json")
            activityLogFilePath.setWritable(true)

             // if app root directory does not exist, make it
             if(!appFilePath.isDirectory){
                 appFilePath.mkdir()
             }
             // if there is not a directory for the current day, make one
             if(!currentDateFile.isDirectory){
                 currentDateFile.mkdir()
             }

             // if an activity log for the day has not been made, make a blank one and save it
             activityLogFilePath.setWritable(true)


             // load activity log for the day
             var jsonString = File(activityLogFilePath.toString()).readText()
             var dailyActivityLog: DailyActivityLogDTO = gson.fromJson(jsonString, DailyActivityLogDTO::class.java)

             // using a CopyOnWriteArrayList prevents an error where more than one thread is operating on the list, leading to an error
             val syncedActivityLog =  CopyOnWriteArrayList(dailyActivityLog.activityLog)
             val syncedActivityNames = CopyOnWriteArrayList<String>();

             // create a list of all activity names
             for(activity in syncedActivityLog){
                 syncedActivityNames.add(activity.activityName)
             }

             //check if list of names contains the entered value
             val isNameDuplicated = syncedActivityNames.contains(newActivityName.toString())


             var activityName = newActivityName.toString()
             var activityDuration = newActivityLength.toString().toInt()
             // if name is  duplicated remove the existing activity and add its duration to the new activity
             if(isNameDuplicated){
                 var indexOfDuplicatedName = syncedActivityNames.indexOf(newActivityName.toString())
                 var duplicateActivity = dailyActivityLog.activityLog.get(indexOfDuplicatedName)
                 dailyActivityLog.activityLog.removeAt(indexOfDuplicatedName);
                 activityDuration += duplicateActivity.activityDuration
             }

             var combinedActivity = ActivityDTO(activityName,activityDuration)
             combinedActivity.activityName = activityName
             combinedActivity.activityDuration = activityDuration
             dailyActivityLog.activityLog.add(combinedActivity)
             jsonString = gson.toJson(dailyActivityLog)
             File(activityLogFilePath.toString()).writeText(jsonString)

             txtActivity.text = null
             txtTimeSpent.text = null
        }

        fun viewActivityHistory(view: View){
            val intent = Intent(this, ViewActivityHistory::class.java)
            startActivity(intent)
        }


    }
