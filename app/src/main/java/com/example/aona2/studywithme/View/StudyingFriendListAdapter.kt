package com.example.aona2.studywithme.View

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aona2.studywithme.Model.CurrentStudyInfo
import com.example.aona2.studywithme.R
import com.example.aona2.studywithme.TimeManage.CalcRemainTime
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclerview_item.view.*

//HomeActivityの勉強中の友達リストのアダプター
class StudyingFriendListAdapter internal constructor(context: Context, listener: Listener)
    : RecyclerView.Adapter<StudyingFriendListAdapter.StudyingFriendViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    //現在の勉強情報　HomeActivityから更新される
    private var currentStudyInfos = mutableMapOf<String, CurrentStudyInfo>()

    private var currentStudyInfosList = emptyList<Pair<String, CurrentStudyInfo>>()

    private val clickListener: Listener = listener

    inner class StudyingFriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val userIcon = itemView.user_icon
        val userName = itemView.user_name
        val taskName = itemView.task_name
        val remainTime = itemView.remain_time
        val taskStatusIcon = itemView.task_status_icon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudyingFriendViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return StudyingFriendViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: StudyingFriendViewHolder, position: Int) {
        val currentStudyInfo = currentStudyInfosList[position].second
        //タスク名を入力
        holder.taskName.text = currentStudyInfo.taskName

        //HomeActivityのusersを使用する
        val user = HomeActivity.users[currentStudyInfo.uid]
        if(user == null) return

        //ユーザーアイコンとユーザー名を入力
        Picasso.get().load(user!!.userImageView).into(holder.userIcon)
        holder.userName.text = user!!.userName

        //残り時間を計算
        val remainTime = CalcRemainTime(currentStudyInfo.roomStartAt).calcRemainTime()
        //勉強中か？のステータスアイコンを出す
        if (remainTime.second) holder.taskStatusIcon.setImageResource(R.drawable.study_status)
        else holder.taskStatusIcon.setImageResource(R.drawable.breaktime_status)
        //残り時間を表示　ミリ秒->分に変換
        holder.remainTime.text = "${remainTime.first / 60000}分"

        //クリックされたら、ポジションと勉強情報をHomeActivityに送信
        holder.itemView.setOnClickListener {
            clickListener.onItemClicked(holder.adapterPosition, currentStudyInfo)
        }
    }

    //フィールドに現在の勉強情報を保持　変更があれば自動で更新する(まだされない)
    //HomeActivityから呼ばれる
    internal fun setCurrentStudyInfos(currentStudyInfos: MutableMap<String, CurrentStudyInfo>){
        this.currentStudyInfos = currentStudyInfos
        currentStudyInfosList = currentStudyInfos.toList()
        notifyDataSetChanged()
    }

    override fun getItemCount() = currentStudyInfos.size

    //itemがクリックされたか
    interface Listener{
        fun onItemClicked(index: Int, currentStudyInfo: CurrentStudyInfo)
    }
}