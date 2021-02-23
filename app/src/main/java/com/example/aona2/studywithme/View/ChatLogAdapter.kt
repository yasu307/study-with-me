package com.example.aona2.studywithme.View

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aona2.studywithme.Model.ChatMessage
import com.example.aona2.studywithme.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

//休憩中に表示されるチャットログのrecyclerViewに適用されるadapter
class ChatLogAdapter internal constructor(context: Context)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private var chatLog = mutableListOf<ChatMessage>()

    //viewTypeとして使用
    //自分が送信したitemに対してはFROM、自分以外はTO
    private val VIEW_TYPE_FROM = 0
    private val VIEW_TYPE_TO = 1

    //自分のuid
    private val uid = Firebase.auth.currentUser?.uid

    inner class ChatFromRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iconFrom = itemView.imageView_chatFromRow
        val messageFrom = itemView.textView_chatFromRow
    }

    inner class ChatToRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val iconTo = itemView.imageView_chatToRow
        val messageTo = itemView.textView_chatToRow
    }

    //ViewTypeに対応したレイアウトを返す
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            VIEW_TYPE_FROM -> {
                val itemView = inflater.inflate(R.layout.chat_from_row, parent, false)
                return ChatFromRowViewHolder(itemView)
            }
            VIEW_TYPE_TO -> {
                val itemView = inflater.inflate(R.layout.chat_to_row, parent, false)
                return ChatToRowViewHolder(itemView)
            }
            else -> {//それ以外（ないはず）
                val itemView = inflater.inflate(R.layout.chat_from_row, parent, false)
                return ChatFromRowViewHolder(itemView)
            }
        }
    }

    //chatMessageに対応したViewTypeを返す
    //自分が送信したitemに対してはFROM、自分以外はTO
    override fun getItemViewType(position: Int): Int {
        if(chatLog[position].fromId == uid) return VIEW_TYPE_FROM
        else return VIEW_TYPE_TO
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chatMessage = chatLog[position]
        if (chatMessage.fromId == uid) { //自分が送信したchatMessage
            val holder = holder as ChatFromRowViewHolder
            val user = HomeActivity.users[uid]
            Picasso.get().load(user?.userImageView).into(holder.iconFrom)
            holder.messageFrom.text = chatMessage.text
        }else{ //自分以外が送信したchatMessage
            val holder = holder as ChatToRowViewHolder
            val user = HomeActivity.users[chatMessage.fromId]
            Picasso.get().load(user?.userImageView).into(holder.iconTo)
            holder.messageTo.text = chatMessage.text
        }
    }

    override fun getItemCount(): Int = chatLog.size

    //フィールドにchatLogを保持　変更があれば自動で更新される
    //StudyActivityから呼ばれる
    internal fun setChatLog(chatLog: MutableList<ChatMessage>){
        this.chatLog = chatLog
//        chatLog.forEach {
//            Log.d("ChatLogAdapter", "chat from is ${it.fromId}")
//            Log.d("ChatLogAdapter", "chat message is ${it.text}")
//        }
        notifyDataSetChanged()
    }
}