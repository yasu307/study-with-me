package com.example.aona2.studywithme

import android.graphics.Color
import android.media.SoundPool
import android.os.CountDownTimer
import android.widget.TextView

class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long, private val timerText: TextView) : CountDownTimer(millisInFuture, countDownInterval){

    override fun onTick(p0: Long) {
        //変わらない
        timerText.text = "test"
    }

    override fun onFinish() {
        timerText.text = "00:00"
    }
}


//class MyCountDownTimer(
//    millisInFuture: Long,
//    countDownInterval: Long,
//    var mTimerText: TextView,
//    var mSoundPool: SoundPool,
//    var mSoundResId: Int
//) :
//    CountDownTimer(millisInFuture, countDownInterval) {
//    override fun onTick(millisUntilFinished: Long) {
//        val minute = millisUntilFinished / 1000 / 60
//        val second = millisUntilFinished / 1000 % 60
//        mTimerText.text = String.format("%1d:%2$02d", minute, second)
//        if (millisUntilFinished <= 1000 * 30) mTimerText.setTextColor(Color.RED) else if (millisUntilFinished <= 1000 * 60) mTimerText.setTextColor(
//            Color.YELLOW
//        ) else mTimerText.setTextColor(Color.BLACK)
//    }
//
//    override fun onFinish() {
//        mTimerText.text = "0:00"
//        mSoundPool.play(mSoundResId, 1.0f, 1.0f, 0, 0, 1.0f)
//    }
//
//}

