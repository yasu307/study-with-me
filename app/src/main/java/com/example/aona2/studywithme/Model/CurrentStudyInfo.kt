package com.example.aona2.studywithme.Model

import android.os.Parcelable
import android.widget.ImageView
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CurrentStudyInfo(val uid: String,
                       val roomId: String,
                       val taskName: String,
                       val roomStartAt: Long,
                       val studyStartAt: Long) : Parcelable {
    constructor() : this("","","",0,0)
}