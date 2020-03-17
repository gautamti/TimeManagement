package com.example.timemanagementapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.timemanagementapp.DTO.ActivityDTO
import com.example.timemanagementapp.DTO.DailyActivityLogDTO
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
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
            val newActivityName = txtActivity.text;
            val newActivityLength = txtTimeSpent.text
            var appFilePath = filesDir
            val sdf = SimpleDateFormat("dd.M.yyyy")
            val currentDate = sdf.format(Date())
            val currentDateDirectoryName = "$appFilePath/$currentDate"
            val currentDateFile = File(currentDateDirectoryName)
            val gson = Gson()

            Log.d("Log", txtActivity.text.toString())
            Log.d("Log", txtTimeSpent.text.toString())
            Log.d("Log", appFilePath.toString())
            Log.d("Log", currentDate)
            Log.d("Log", currentDateDirectoryName)

            // if app root directory does not exist, make it
           if(!appFilePath.isDirectory){
               appFilePath.mkdir()
               Log.d("Log","made app root directory")
           }
            // if there is not a directory for the current day, make one
            if(!currentDateFile.isDirectory){
                currentDateFile.mkdir()
                Log.d("Log","made $appFilePath/$currentDate directory")
            }

            // if an activity log for the day has not been made, make a blank one and save it
            val activityLogFilePath = File("$currentDateFile/$currentDate.json")
            activityLogFilePath.setWritable(true)
            if(!activityLogFilePath.exists()){
                Log.d("Log","new activity log made")

                // Gson needs the file to not be null so a default activity log needs to be created
                var newActivityLogList = ArrayList<ActivityDTO>()
                var defaultActivity = ActivityDTO("defaultName",0)
                newActivityLogList.add(defaultActivity)
                var dailyActivityLog = DailyActivityLogDTO(currentDate,newActivityLogList)

                // converts the object to a JSON string
                var jsonString = gson.toJson(dailyActivityLog)
                File(activityLogFilePath.toString()).writeText(jsonString)

            }


            // load activity log for the day
            var jsonString = File(activityLogFilePath.toString()).readText()
            val dailyActivityLog: DailyActivityLogDTO = gson.fromJson(jsonString, DailyActivityLogDTO::class.java)

            // using a CopyOnWriteArrayList prevents an error where more than one thread is operating on the list, leading to an error
            val syncedActivityLog =  CopyOnWriteArrayList(dailyActivityLog.activityLog)
             // iterate through the list of activities for the day, if it is a new activity add it, if is an existing one, add the duration
            for (activity in syncedActivityLog){

                if (activity.activityName == newActivityName.toString() && activity.activityName != null){
                    activity.activityDuration = activity.activityDuration.plus(newActivityLength.toString().toInt())
                    var jsonString = gson.toJson(dailyActivityLog)
                    File(activityLogFilePath.toString()).writeText(jsonString)
                    Log.d("Log", "that activity already exists, added more time to existing activity")
                }

                // removes the default activity if it exists
                if (activity.activityName == "defaultName"){
                    dailyActivityLog.activityLog.remove(activity)
                }
                else{
                    val newActivity = ActivityDTO("exampleActivity", 1)
                    newActivity.activityName = newActivityName.toString()
                    newActivity.activityDuration = newActivityLength.toString().toInt()
                    dailyActivityLog.activityLog.add(newActivity)
                    var jsonString = gson.toJson(dailyActivityLog)
                    File(activityLogFilePath.toString()).writeText(jsonString)
                    Log.d("Log", "added new activity")
                }

            }


        }
    }
