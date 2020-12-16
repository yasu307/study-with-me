package com.example.aona2.studywithme

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.LocalDateTime

class StudyActivity : AppCompatActivity() {
    lateinit var startRoomAt: LocalDateTime

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
        Log.d("StudyActivity", "onCreate")

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ){
            startRoomAt = LocalDateTime.of(2020, 12, 16, 15, 0,0, 0)
            Log.d("StudyActivity", "start room at $startRoomAt")
        }

        val remainTime = calcRemainTime()

    }

    private fun calcRemainTime(){
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
            val currentTime = LocalDateTime.now()
            Log.d("StudyActivity", "current time is $currentTime ")
            val elapsedTime = Duration.between(startRoomAt, currentTime ).seconds
            Log.d("StudyActivity", "elapsed time is $elapsedTime")
            val remainTime = 30 - (elapsedTime % 30)
            Log.d("StudyActivity", "remain time is $remainTime")
        }
    }
}