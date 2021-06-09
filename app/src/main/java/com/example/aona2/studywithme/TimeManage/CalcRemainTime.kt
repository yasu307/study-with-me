package com.example.aona2.studywithme.TimeManage

import android.util.Log
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

//roomの開始時間から残り時間の計算をするクラス
//今のところ必ずcalcRemainTime()を呼び出す CalcRemainTime(millis).calcRemainTime()
class CalcRemainTime(private val startRoomAtMillis: Long)  {
    //一周の時間
    private val oneRoopMin = 30
    //休憩の時間
    //つまり　勉強時間 = oneRoopMin - breaktimeMin
    private val breaktimeMin = 5

    //ログで時間を表示するときのフォーマット
    private val simpleDateFormat = SimpleDateFormat("yyyy年MM月dd日 HH時mm分ss.SSS秒")

    //現在実行中のモードの残り時間を計算　返り値　Pair<残り時間（ミリ秒）,勉強中か？>
    //もっと簡素にできないか？
    fun calcRemainTime() : Pair<Long, Boolean>{
        Log.d("CalcRemainTime", "calcRemainTime()")
        //現在の時間
        val currentTimeMillis = Calendar.getInstance().timeInMillis
//        Log.d("StudyActivity", "current time is ${simpleDateFormat.format(currentTimeMillis)} ")

        //経過時間
        val elapsedTimeMillis = currentTimeMillis - startRoomAtMillis
//        Log.d("StudyActivity", "elapsed time millis is $elapsedTimeMillis")

        //残り時間
        val remainTimeMillis = ((oneRoopMin * 60 * 1000) - (elapsedTimeMillis % (oneRoopMin * 60 * 1000)))
//        Log.d("StudyActivity", "remain time is $remainTimeMillis")

        //残り時間が5分以上なら勉強中、そうでないなら休憩中
        if (remainTimeMillis >= breaktimeMin * 60 * 1000) return Pair(remainTimeMillis - breaktimeMin * 60 * 1000, true)
        else return Pair(remainTimeMillis, false)
    }
}