package com.example.aona2.studywithme.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StudyInfo(val uid: String,
                            val taskName: String,
                            val studyStartAt: Long): Parcelable {
    constructor() : this("","",-1)
}