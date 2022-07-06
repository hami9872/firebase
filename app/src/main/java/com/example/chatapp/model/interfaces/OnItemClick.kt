package com.example.chatapp.model.interfaces

import com.example.chatapp.Adapter

interface OnItemClick {
    fun onClick(pos: Int, status: Adapter.ClickStatus)
}