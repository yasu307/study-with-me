package com.example.aona2.studywithme.View

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.aona2.studywithme.Model.StudyInfo
import com.example.aona2.studywithme.R
import com.example.aona2.studywithme.TimeManage.CalcStudyTime
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_study_log.*
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

class StudyLogActivity : AppCompatActivity() {
    val calcStudyTime = CalcStudyTime()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_study_log)

        fetchMyStudyInfo()

        val studyTime = listOf<Float>(6.7f, 1f, 10.4f, 3.4f, 50f, 0.1f, 4.5f)//X軸データ

    }

    private fun fetchMyStudyInfo(): MutableList<StudyInfo>{
        Log.d("StudyLogActivity", "fetch my study info")
        var studyInfoList = mutableListOf<StudyInfo>()
        val myUid = Firebase.auth.currentUser?.uid ?: return studyInfoList
        val studyInfoLogRef = Firebase.database.getReference("studyInfoLog/$myUid")

        studyInfoLogRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val studyInfo = it.getValue(StudyInfo::class.java)
                    if(studyInfo != null) studyInfoList.add(studyInfo)
                }
                calcStudyTime.studyInfoList = studyInfoList
                calcStudyTime.setWeeklyStudyTime()
                setChart(calcStudyTime.weeklyStudyHour)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
        return studyInfoList
    }

    private fun setChart(list: List<Float>){
        for(wsh in calcStudyTime.weeklyStudyHour)
        Log.d("StudyLogActivity", wsh.toString())
        val entryList = List(list.size){BarEntry(it.toFloat(), list[it])}
        val barDataSet = BarDataSet(entryList, "square").apply {
            color = Color.BLUE
            setDrawValues(false)
        }
        val barData = BarData(listOf(barDataSet))

        val today = LocalDate.now()
        val dayStr = List(7){today.minusDays(it.toLong()).dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)}.reversed()
        study_log_chart.apply {
            data = barData
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(dayStr)
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM
            }
            axisRight.apply {
                axisMinimum = 0f
                axisMaximum = 12f
                labelCount = 4
                valueFormatter = object: ValueFormatter(){
                    override fun getFormattedValue(value: Float): String{
                        return value.toInt().toString() + "時間"
                    }
                }
            }
            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 12f
                isEnabled = false
            }
            legend.isEnabled = false
            description.isEnabled = false
            setTouchEnabled(false)
            animateY(2000)
            invalidate()
        }
    }
}