package com.example.aona2.studywithme.TimeManage

import android.util.Log
import com.example.aona2.studywithme.Model.StudyInfo
import java.util.*

class CalcStudyTime(val studyInfoList: MutableList<StudyInfo>) {
    //一週間の勉強時間 (一昨日の勉強時間,昨日の勉強時間,今日の勉強時間)
    var weeklyStudyTime = mutableListOf<Long>(0,0,0,0,0,0,0)
    var now = Calendar.getInstance().timeInMillis

    companion object {
        //デバッグ時のコメント用
        val TAG = "CalcStudyTime"
    }
    fun logStudyInfoList(){
        Log.d(TAG, "log study info list")
        for (studyInfo in studyInfoList) {
            Log.d(TAG, studyInfo.toString())
        }
    }
    //startTimeとfinishTimeの間の時間をweelyStudyTimeに追加していく
    fun addStudyTime(startTime: Long, finishTime: Long){
    }
}