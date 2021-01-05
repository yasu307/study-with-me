package com.example.aona2.studywithme

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_study.*
import java.time.Duration
import java.time.LocalDateTime

class StudyActivity : AppCompatActivity() {
    private lateinit var startRoomAt: LocalDateTime
    private var myCountDownTimer: MyCountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study)

        Log.d("StudyActivity", "onCreate")

        //recyclerviewの設定
        val adapter = InRoomFriendListAdapter(this)
        in_room_friend_recyclerview.adapter = adapter
        in_room_friend_recyclerview.layoutManager = LinearLayoutManager(this)
        //recyclerViewに枠線をつける
        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        in_room_friend_recyclerview.addItemDecoration(itemDecoration)

        //LocalDateTImeがオレオ以上でしか使えないので仕方なくバージョン確認
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ){
            //テスト用にダミーのルーム開始時間を設定
            startRoomAt = LocalDateTime.of(2021, 1, 2, 15, 13,0, 0)
            Log.d("StudyActivity", "start room at $startRoomAt")
        }

        startTimer()
    }

    //現在実行中のモードの残り時間を計算　返り値　Pair<残り時間（ミリ秒）,勉強中か？>
    //もっと簡素にできないか？
    //MyCountDownTImerに移行すべき？
    private fun calcRemainTime() : Pair<Long, Boolean>{
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
            //現在の時間
            val currentTime = LocalDateTime.now()
            Log.d("StudyActivity", "current time is $currentTime ")
            //経過時間
            val elapsedTimeMillis = Duration.between(startRoomAt, currentTime ).toMillis()
            Log.d("StudyActivity", "elapsed time millis is $elapsedTimeMillis")
            //残り時間
            val remainTimeMillis = ((30 * 60 * 1000) - (elapsedTimeMillis % (30 * 60 * 1000)))
            Log.d("StudyActivity", "remain time is $remainTimeMillis")

            //残り時間が5分以上なら勉強中、そうでないなら休憩中
            if(remainTimeMillis >= 5 * 60 * 1000) return Pair(remainTimeMillis - 5 * 60 * 1000, true)
            else return Pair(remainTimeMillis, false)
        } else {
            //バージョン確認がなくなったらここは削除する
            //適当な値を返す
            val remainTimeMillis = 0
            return Pair(remainTimeMillis.toLong(), true)
        }
    }

    //タイマーの起動
    fun startTimer() {
        //最初のタイマー起動の場合はキャンセルしない
        if(myCountDownTimer != null)  myCountDownTimer?.cancel()

        //タイマーに設定する残り時間を計算
        val remainTime:Pair<Long, Boolean> = calcRemainTime()

        //タイマーをセット、開始
        myCountDownTimer = MyCountDownTimer(remainTime.first, 1000, remainTime.second, remain_time_textView, remain_time_progressBar, this)
        myCountDownTimer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        //一応タイマーをキャンセルしておく
        if(myCountDownTimer != null)  myCountDownTimer?.cancel()
    }
}