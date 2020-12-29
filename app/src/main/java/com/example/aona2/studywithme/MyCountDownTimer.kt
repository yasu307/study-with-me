package com.example.aona2.studywithme

import android.graphics.Color
import android.media.SoundPool
import android.os.CountDownTimer
import android.widget.ProgressBar
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class MyCountDownTimer(
        millisInFuture: Long,
        countDownInterval: Long,
        private val timerText: TextView,
        private val timerProgressBar: ProgressBar
    ) : CountDownTimer(millisInFuture, countDownInterval){
    //分秒表示のフォーマットを作成
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.JAPAN)

    init {
        //プログレスバーのマックスを指定 30分をミリ秒に変換したもの
        timerProgressBar.max = 30 * 60 * 1000
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
        timerText.text = "Finish!!"
    }
}

