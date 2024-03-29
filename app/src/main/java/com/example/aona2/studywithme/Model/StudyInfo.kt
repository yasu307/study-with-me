package com.example.aona2.studywithme.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StudyInfo(val uid: String,
                     val taskName: String,
                     val studyStartAt: Long,
                     val roomId: String,
                     val roomStartAt: Long,
                     var studyFinishAt: Long) : Parcelable {
    constructor() : this("", "", -1, "", -1, -1)

    override fun toString(): String {
        return "uid is $uid, task name is $taskName, study start at $studyStartAt, room id is $roomId, room start at $roomStartAt"
    }
}