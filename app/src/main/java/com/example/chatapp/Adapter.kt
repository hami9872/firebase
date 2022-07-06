package com.example.chatapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.model.Post
import com.example.chatapp.model.firebase.CreatePostActivity
import com.example.chatapp.model.interfaces.OnItemClick
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*


class Adapter(val context: Context, val list: MutableList<Post> , val listener : OnItemClick) :
    RecyclerView.Adapter<Adapter.Holder>() {
    enum class ClickStatus{
        ITEM_CLICK,
        DELETE
    }

    data class Holder(val itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView = itemView.findViewById<TextView>(R.id.title)
        var iv = itemView.findViewById<ImageView>(R.id.deleteIV)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.adapter_details, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        var post = list[position]
        holder.textView.text = post.title
        holder.textView.setOnClickListener(View.OnClickListener {
            listener.onClick(position ,ClickStatus.ITEM_CLICK )
        })

        holder.iv.setOnClickListener(View.OnClickListener {
            listener.onClick(position ,ClickStatus.DELETE )
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }
}