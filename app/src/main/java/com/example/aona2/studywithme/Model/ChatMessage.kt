package com.example.aona2.studywithme.Model

data class ChatMessage(val id: String, val text: String, val roomId: String, val fromId: String, val timeStamp: Long) {
    constructor() : this("", "", "", "", -1)
}