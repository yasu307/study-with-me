package com.example.aona2.studywithme.TimeManage

import android.content.Context
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.aona2.studywithme.R
import com.example.aona2.studywithme.View.StudyActivity
import kotlinx.android.synthetic.main.activity_study.*
import java.text.SimpleDateFormat
import java.util.*

class MyCountDownTimer(
        millisInFuture: Long,
        countDownInterval: Long,
        private val isStudying : Boolean,
        private val timerText: TextView,
        private val timerProgressBar: ProgressBar,
        private val studyActivity: StudyActivity
    ) : CountDownTimer(millisInFuture, countDownInterval){
    //分秒表示のフォーマットを作成
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.JAPAN)
    //プログレスバーのmaxを変数にしておく
    //勉強中なら25分、休憩中なら5分を指定
    private val study_max = 25 * 60 * 1000
    private val breaktime_max = 5 * 60 * 1000

    init {
        //プログレスバーのマックスを指定 30分をミリ秒に変換したもの
        //constraintLayoutの背景色をモードによって変更
        if(isStudying){
            timerProgressBar.max = study_max
//            //色変更を一時停止
//            studyActivity.showRemainTime_constraint_studyActivity.setBackgroundColor(ContextCompat.getColor(studyActivity, R.color.study))
        }
        else{
            timerProgressBar.max = breaktime_max
//            //色変更を一時停止
//            studyActivity.showRemainTime_constraint_studyActivity.setBackgroundColor(ContextCompat.getColor(studyActivity, R.color.breakTime))
        }
        //プログレスバー反転　表示が減っていくように
        timerProgressBar.rotation = 180F
    }

    override fun onTick(millisUntilFinished: Long) {
        //ミリ秒から分秒（String）への変換
        val timeString = dateFormat.format(millisUntilFinished)

        //テキストに現在の分秒を表示
        timerText.text = timeString

        //プログレスバーを更新
        timerProgressBar.progress = millisUntilFinished.toInt()
    }

    override fun onFinish() {
        val vibrator = studyActivity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Log.d("MyCountDownTimer", "vibration effect")
            val vibrationEffect = VibrationEffect.createWaveform(longArrayOf(1000, 0, 1000), intArrayOf(DEFAULT_AMPLITUDE, 0, DEFAULT_AMPLITUDE), -1)
            vibrator.vibrate(vibrationEffect)
        } else {
            Log.d("MyCountDownTimer", "vibrator vibrate")
            vibrator.vibrate(longArrayOf(0, 1000, 400, 1000), -1)
        }

        //次のタイマーをスタート
        studyActivity.startTimer()
        Log.d("MyCountDownTimer","on finish")
    }
}

