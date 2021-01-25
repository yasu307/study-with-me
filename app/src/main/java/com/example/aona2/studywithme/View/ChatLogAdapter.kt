package com.example.aona2.studywithme.View

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aona2.studywithme.Model.User
import com.example.aona2.studywithme.R
import kotlinx.android.synthetic.main.chat_from_row.view.*

class ChatLogAdapter internal constructor(context: Context)
    : RecyclerView.Adapter<ChatLogAdapter.ChatLogViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    //ルームにいる人のリスト　StudyActivityから更新される
    private var inRoomUsersList: MutableList<User> = mutableListOf<User>()

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
    }

    override fun getItemCount(): Int = 100

    //フィールドにルームにいるユーザーを保持　変更があれば自動で更新される
    //StudyActivityから呼ばれる
    internal fun setInRoomUsers(inRoomUsersList: MutableList<User>){
        this.inRoomUsersList = inRoomUsersList
        notifyDataSetChanged()
    }
}