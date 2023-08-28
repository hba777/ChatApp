package com.example.chats.data

//data class Messages(
//    val senderId: String,
//    val receiverId: String,
//    val text: String,
//   // val timesstamp: Long
//)
data class Messages(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val messageText: String = "",
    val timestamp: Long = 0
)

