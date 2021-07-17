package com.example.aona2.studywithme.View

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.aona2.studywithme.Model.CurrentStudyInfo
import com.example.aona2.studywithme.Model.Room
import com.example.aona2.studywithme.Model.StudyInfo
import com.example.aona2.studywithme.R
import com.example.aona2.studywithme.TimeManage.CalcRemainTime
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recyclerview_item.view.*
import kotlinx.android.synthetic.main.room_row.view.*

//HomeActivityの勉強中の友達リストのアダプター
class StudyingFriendListAdapter internal constructor(val context: Context, listener: Listener)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    //すべての勉強情報　HomeActivityから更新される
    private var allStudyInfos = mutableListOf<StudyInfo>()

    private var rooms = mutableMapOf<String, Room>()

    private val clickListener: Listener = listener

    //viewTypeとして使用
    //部屋分けのためのRoom表示
    private val VIEW_TYPE_ROOM = 0
    //勉強中の友達
    private val VIEW_TYPE_STUDY_INFO = 1

    //勉強中の友達を表示する行のViewHolder
    inner class StudyingFriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val userIcon = itemView.user_icon
        val userName = itemView.user_name
        val taskName = itemView.task_name
        val remainTime = itemView.remain_time
        val taskStatusIcon = itemView.task_status_icon
    }

    //部屋分けのための行のViewHolder
    inner class RoomViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val roomText = itemView.room_textView_roomRow
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType){
            VIEW_TYPE_ROOM ->{//部屋分けの行
                val itemView = inflater.inflate(R.layout.room_row, parent, false)
                return RoomViewHolder(itemView)
            }
            VIEW_TYPE_STUDY_INFO ->{//勉強している友達を表示する行
                val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
                return StudyingFriendViewHolder(itemView)
            }
            else ->{//それ以外だった場合（ないはず）
                val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
                return StudyingFriendViewHolder(itemView)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder.itemViewType){
            VIEW_TYPE_ROOM ->{//部屋分けの行
                //ViewHolderをキャスト
                val holder = holder as RoomViewHolder

                val studyInfo = allStudyInfos[position]
                holder.roomText.text = "ルーム${studyInfo.roomId}"
            }
            VIEW_TYPE_STUDY_INFO ->{//勉強している友達を表示する行
                //ViewHolderをキャスト
                val holder = holder as StudyingFriendViewHolder

                val studyInfo = allStudyInfos[position]
                //タスク名を入力
                holder.taskName.text = studyInfo.taskName

                //HomeActivityのusersを使用する
                val user = HomeActivity.users[studyInfo.uid] ?: return

                //ユーザーアイコンとユーザー名を入力
                Picasso.get().load(user.userImageView).into(holder.userIcon)
                holder.userName.text = user.userName

                //残り時間を計算
                val remainTime = CalcRemainTime(studyInfo.roomStartAt).calcRemainTime()
                //勉強中か？のステータスアイコンを出す
                if (remainTime.second) Glide.with(context).load(R.drawable.edit_animation).into(holder.taskStatusIcon);
                else holder.taskStatusIcon.setImageResource(R.drawable.breaktime_status)
                //残り時間を表示　ミリ秒->分に変換
                holder.remainTime.text = "${remainTime.first / 60000}分"

                //クリックされたら、ポジションと勉強情報をHomeActivityに送信
                holder.itemView.setOnClickListener {
                    clickListener.onItemClicked(holder.adapterPosition, studyInfo.uid, rooms[studyInfo.roomId]?: Room())
                }
            }
        }

    }

    //フィールドに現在の勉強情報を保持　変更があれば自動で更新する(まだされない)
    //HomeActivityから呼ばれる
    internal fun setRooms(rooms: MutableMap<String, Room>){
        Log.d("StudyingFriendListAdapter", "set rooms")
        this.rooms = rooms
        val tmpStudyInfos = mutableListOf<StudyInfo>()
        //Room番号の初期化
        var roomNum = 1
        rooms.forEach { roomMap ->
            //roomの切り替わりを示すために特殊なStudyInfoをリストに格納する
            val roomStudyInfo = StudyInfo("room","room", "", "" , roomNum.toLong(), "")
            //次のRoom番号のために1を足す
            roomNum++
            tmpStudyInfos.add(roomStudyInfo)
            //そのRoomにいるユーザーのStudyInfoをListに格納
            roomMap.value.studyInfos.forEach { studyInfoMap ->
                tmpStudyInfos.add(studyInfoMap.value)
                Log.d("StudyingFriendListAdapter", "user id is ${studyInfoMap.value.uid}")
                Log.d("StudyingFriendListAdapter", "user name is ${HomeActivity.users[studyInfoMap.value.uid]?.userName}")
            }
        }
        allStudyInfos = tmpStudyInfos
        notifyDataSetChanged()
    }

    //chatMessageに対応したViewTypeを返す
    //自分が送信したitemに対してはFROM、自分以外はTO
    override fun getItemViewType(position: Int): Int {
        if(allStudyInfos[position].uid == "room"){
            return VIEW_TYPE_ROOM
        }else{
            return VIEW_TYPE_STUDY_INFO
        }
    }

    override fun getItemCount() = allStudyInfos.size

    //itemがクリックされたか
    interface Listener{
        fun onItemClicked(index: Int, friendUid: String, friendRoom: Room)
    }
}