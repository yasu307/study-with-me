package com.example.aona2.studywithme.View

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aona2.studywithme.Model.ChatMessage
import com.example.aona2.studywithme.Model.User
import com.example.aona2.studywithme.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogAdapter internal constructor(context: Context)
    : RecyclerView.Adapter<ChatLogAdapter.ChatLogViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    //ルームにいる人のリスト　StudyActivityから更新される
    private var inRoomUsersList = mutableListOf<User>()

    private var chatLog = mutableListOf<ChatMessage>()

    private val VIEW_TYPE_FROM = 0
    private val VIEW_TYPE_TO = 1


    inner class ChatLogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatLogAdapter.ChatLogViewHolder {
        val itemView = when(viewType){
            VIEW_TYPE_FROM ->
                inflater.inflate(R.layout.chat_from_row, parent, false)
            VIEW_TYPE_TO ->
                inflater.inflate(R.layout.chat_to_row, parent, false)
            else ->
                inflater.inflate(R.layout.chat_from_row, parent, false)
        }
        return ChatLogViewHolder(itemView)
    }

    override fun getItemViewType(position: Int): Int {
        if(position % 2 == 0){
            return VIEW_TYPE_FROM
        }
        else{
            return VIEW_TYPE_TO
        }
    }

    override fun onBindViewHolder(holder: ChatLogViewHolder, position: Int) {
        val uid = Firebase.auth.currentUser?.uid
        val user = HomeActivity.users[uid]
        if(position % 2 == 0){
            val iconFrom = holder.itemView.imageView_chatFromRow
            val messageFrom = holder.itemView.textView_chatFromRow
            Picasso.get().load(user?.userImageView).into(iconFrom)
        }
        else{
            val iconTo = holder.itemView.imageView_chatToRow
            val messageTo = holder.itemView.textView_chatToRow
            Picasso.get().load(user?.userImageView).into(iconTo)
        }
    }

    override fun getItemCount(): Int = 100

    internal fun setChatLog(chatLog: MutableList<ChatMessage>){
        this.chatLog = chatLog
        chatLog.forEach {
            Log.d("ChatLogAdapter", "chat from is ${it.fromId}")
            Log.d("ChatLogAdapter", "chat message is ${it.text}")
        }
        notifyDataSetChanged()
    }
}