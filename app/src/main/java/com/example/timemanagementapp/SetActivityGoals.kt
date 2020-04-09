package com.example.timemanagementapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.text.set
import com.example.timemanagementapp.DTO.ActivityGoalDTO
import com.example.timemanagementapp.DTO.ActivityGoalList
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_set_goals.*
import java.io.File
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class SetActivityGoals : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_goals)

        var appFilePath = filesDir
        val goalActivityListFileName = "$appFilePath/ActivityGoals"
        val goalActivityListFile = File(goalActivityListFileName)
        val gson = Gson()

        // if app root directory does not exist, make it
        if (!appFilePath.isDirectory) {
            appFilePath.mkdir()
        }
        // if there is not a directory for the current day, make one
        if (!goalActivityListFile.isDirectory) {
            goalActivityListFile.mkdir()
        }

        // if a goal list has not been made, create a blank one
        val goalListFilePath = File("$goalActivityListFile/goals.json")
        goalListFilePath.setWritable(true)
        if (!goalListFilePath.exists()) {

            // Gson needs the file to not be null so a default activity log needs to be created
            var newGoalList = ArrayList<ActivityGoalDTO>()
            var defaultActivityGoal = ActivityGoalDTO("DEFAULTACTIVITYGOALNAME", 0, 0)
            newGoalList.add(defaultActivityGoal)
            var activityGoalList = ActivityGoalList(newGoalList)

            // converts the object to a JSON string
            var jsonString = gson.toJson(activityGoalList)
            File(goalListFilePath.toString()).writeText(jsonString)
        }

        updateGoals()
    }

    fun backToAddActivity(view: View){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun toggleAddRemoveGoal(view: View){
        // if the toggle was just turned off the user is switching from adding to removing
        if(!toggleAddRemoveButton.isChecked){
            activityGoalMin.text = null
            activityGoalMax.text = null
            activityGoalName.text = null
            activityGoalMin.isEnabled = false
            activityGoalMax.isEnabled = false
            submitButton.text = "Delete"
        }

        // if the toggle was just turned on the user is switching from removing to adding
        else{
            activityGoalName.text = null
            activityGoalMin.isEnabled = true
            activityGoalMax.isEnabled = true
            submitButton.text = "Add"
        }

    }

    fun submitGoal(view: View){

        val goalActivityName = activityGoalName.text
        val goalActivityMinLength = activityGoalMin.text
        val goalActivityMaxLength = activityGoalMax.text
        var appFilePath = filesDir
        val goalActivityListFileName = "$appFilePath/ActivityGoals"
        val goalActivityListFile = File(goalActivityListFileName)
        val gson = Gson()

        val goalListFilePath = File("$goalActivityListFile/goals.json")
        goalListFilePath.setWritable(true)


        // load goal list
        var jsonString = File(goalListFilePath.toString()).readText()
        var activityGoalList: ActivityGoalList = gson.fromJson(jsonString, ActivityGoalList::class.java)

        // using a CopyOnWriteArrayList prevents an error where more than one thread is operating on the list, leading to an error
        val syncedActivityLog =  CopyOnWriteArrayList(activityGoalList.activityGoals)
        val syncedActivityNames = CopyOnWriteArrayList<String>();

        // create a list of all activity names
        for(activity in syncedActivityLog){
            syncedActivityNames.add(activity.activityName)
        }

        //check if list of names contains the entered value
        val isNameDuplicated = syncedActivityNames.contains(goalActivityName.toString())


        var activityName = goalActivityName.toString()


        // if name is  duplicated update the values
        if(isNameDuplicated){
            var indexOfDuplicatedName = syncedActivityNames.indexOf(goalActivityName.toString())
            var duplicateActivity = activityGoalList.activityGoals.get(indexOfDuplicatedName)
            activityGoalList.activityGoals.removeAt(indexOfDuplicatedName);
            jsonString = gson.toJson(activityGoalList)
            File(goalListFilePath.toString()).writeText(jsonString)
        }

        //adding goal is on, add the goal
        if (toggleAddRemoveButton.isChecked) {
            var activityMinLength = goalActivityMinLength.toString().toInt()
            var activityMaxLength = goalActivityMaxLength.toString().toInt()
            var activityGoal = ActivityGoalDTO(activityName, activityMinLength, activityMaxLength)
            activityGoal.activityName = activityName
            activityGoal.activityDurationMin = activityMinLength
            activityGoal.activityDurationMax = activityMaxLength

            activityGoalList.activityGoals.add(activityGoal)
            jsonString = gson.toJson(activityGoalList)
            File(goalListFilePath.toString()).writeText(jsonString)
        }


        activityGoalMin.text = null
        activityGoalMax.text = null
        activityGoalName.text = null
        updateGoals()
    }

    fun updateGoals(){
        var appFilePath = filesDir
        val goalActivityListFileName = "$appFilePath/ActivityGoals"
        val goalActivityListFile = File(goalActivityListFileName)
        val gson = Gson()
        val goalListFilePath = File("$goalActivityListFile/goals.json")

        // load goal list
        var jsonString = File(goalListFilePath.toString()).readText()
        var activityGoalList: ActivityGoalList = gson.fromJson(jsonString, ActivityGoalList::class.java)

        var textToBeDisplayed = "Current Goals: "

        for(goal in activityGoalList.activityGoals){
            textToBeDisplayed = textToBeDisplayed + "\n" + goal.activityName + ":"+ " " + "Min Hours: "+ goal.activityDurationMin + " " + "Max Hours: " + goal.activityDurationMax
        }
        activityGoals.setText(textToBeDisplayed)
    }
}
