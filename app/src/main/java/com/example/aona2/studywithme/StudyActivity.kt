package com.example.aona2.studywithme

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_study.*
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
        MyCountDownTimer(remainTime * 1000, 1000, remain_time_textView)
    }

    private fun calcRemainTime() :Long {
        var remainTime: Long = 0
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
            val currentTime = LocalDateTime.now()
            Log.d("StudyActivity", "current time is $currentTime ")
            val elapsedTime = Duration.between(startRoomAt, currentTime ).seconds
            Log.d("StudyActivity", "elapsed time is $elapsedTime")
            remainTime = ((30 * 60) - (elapsedTime % (30 * 60)))
            Log.d("StudyActivity", "remain time is $remainTime")
        }
        return remainTime
    }
}