package com.example.aona2.studywithme.TimeManage

import android.util.Log
import com.example.aona2.studywithme.Model.StudyInfo
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class CalcStudyTime(val studyInfoList: MutableList<StudyInfo>) {
    //一週間の勉強時間 (...,一昨日の勉強時間,昨日の勉強時間,今日の勉強時間)
    var weeklyStudyMinutes = mutableListOf<Long>(0,0,0,0,0,0,0)

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

    //ToDo テストを追加
    fun setWeeklyStudyTime(){
        logStudyInfoList()

        val today = LocalDate.now()
        for (studyInfo in studyInfoList) {
            val startTime = LocalDateTime.parse(studyInfo.studyStartAt)
            val startDate = startTime.toLocalDate()
            val finishTime = LocalDateTime.parse(studyInfo.studyFinishAt)
            //エラーチェック
            if(finishTime.isBefore(startTime)) continue

            val period = withinAWeek(today, startDate)
            if(period < 0) continue
            weeklyStudyMinutes[period] += ChronoUnit.MINUTES.between(startTime, finishTime)
        }
        logWeeklyStudyTime()
    }

    fun logWeeklyStudyTime(){
        var daysago = 0
        for(studyMinutes in weeklyStudyMinutes){
            Log.d(TAG, "$daysago 日前 ${studyMinutes.toString()}　分")
            daysago++
        }
    }

    //一週間以内だった場合何日前か返す、それ以外は-1を返す
    //ToDo テストを追加する
    fun withinAWeek(today: LocalDate, before: LocalDate): Int{
        val period = before.until(today)
        if(period.years == 0 && period.months == 0){
            if(period.days in 0..6) return period.days
        }
        return -1
    }
}