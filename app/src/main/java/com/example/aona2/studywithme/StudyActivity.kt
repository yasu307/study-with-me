package com.example.aona2.studywithme

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_study.*
import java.time.Duration
import java.time.LocalDateTime

class StudyActivity : AppCompatActivity() {
    private lateinit var startRoomAt: LocalDateTime
    private lateinit var myCountDownTImer: MyCountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)
        Log.d("StudyActivity", "onCreate")

        val adapter = InRoomFriendListAdapter(this)
        in_room_friend_recyclerview.adapter = adapter
        in_room_friend_recyclerview.layoutManager = LinearLayoutManager(this)

        //LocalDateTImeがオレオ以上でしか使えないので仕方なくバージョン確認
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ){
            //テスト用にダミーのルーム開始時間を設定
            startRoomAt = LocalDateTime.of(2020, 12, 29, 19, 0,0, 0)
            Log.d("StudyActivity", "start room at $startRoomAt")
        }

        //タイマーに設定する残り時間を計算
        val remainTime = calcRemainTime()

        //タイマーをセット、開始
        myCountDownTImer = MyCountDownTimer(remainTime, 1000, remain_time_textView, remain_time_progressBar)
        myCountDownTImer.start()


    }

    //もっと簡素にできないか？
    //MyCountDownTImerに移行すべき？
    private fun calcRemainTime() :Long {
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
            val currentTime = LocalDateTime.now()
            Log.d("StudyActivity", "current time is $currentTime ")
            val elapsedTimeMillis = Duration.between(startRoomAt, currentTime ).toMillis()
            Log.d("StudyActivity", "elapsed time millis is $elapsedTimeMillis")
            val remainTimeMillis = ((30 * 60 * 1000) - (elapsedTimeMillis % (30 * 60 * 1000)))
            Log.d("StudyActivity", "remain time is $remainTimeMillis")
            return remainTimeMillis
        } else {
            //バージョン確認がなくなったらここは削除する
            val remainTimeMillis = 0
            return remainTimeMillis.toLong()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //一応タイマーをキャンセルしておく
        myCountDownTImer.cancel()
    }
}