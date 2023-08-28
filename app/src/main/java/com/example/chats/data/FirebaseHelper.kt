package com.example.chats.data

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object FirebaseHelper {

    private val database: DatabaseReference = Firebase.database.reference

    fun writeToDatabase(userId: String, email: String, username: String, password: String,profileUrl:String) {

        val dataRef = database.child("Users").child(userId)
        var hashMap: HashMap<String, String> = HashMap()

        hashMap.put("UserId", userId)
        hashMap.put("Email", email)
        hashMap.put("Username", username)
        hashMap.put("Password", password)
        hashMap.put("ProfileUrl",profileUrl)

        dataRef.setValue(hashMap)

    }

    fun readFromDatabase(onDataLoaded: (List<UserData>) -> Unit) {
        val dataRef = database.child("Users")
        dataRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = mutableListOf<UserData>()
                for (userSnapshot in snapshot.children) {
                    val userId = userSnapshot.child("UserId").getValue(String::class.java) ?: ""
                    val email = userSnapshot.child("Email").getValue(String::class.java) ?: ""
                    val username = userSnapshot.child("Username").getValue(String::class.java) ?: ""
                    val password = userSnapshot.child("Password").getValue(String::class.java) ?: ""
                    val profileUrl = userSnapshot.child("ProfileUrl").getValue(String::class.java) ?: ""


                    val userData = UserData(userId, email, username, password,profileUrl)
                    userList.add(userData)
                }
                onDataLoaded(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("FirebaseHelper", "Error reading data from Firebase: ${error.message}")

            }
        })
    }


    fun sendMessage(
        senderId: String,
        receiverId: String,
        messageText: String,
        onComplete: (Boolean) -> Unit
    ) {
        val messageRef = database.child("Messages").push()
        val messageId = messageRef.key ?: ""

        val messageData = Messages(
            messageId,
            senderId,
            receiverId,
            messageText,
            System.currentTimeMillis()
        )

        messageRef.setValue(messageData)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }
    fun readMessagesBetweenUsers(userId: String, receiverId: String, onMessagesReceived: (List<Messages>) -> Unit) {
        val messagesRef = database.child("Messages")

        // Query messages by ordering them by timestamp
        val query = messagesRef.orderByChild("timestamp")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messageList = mutableListOf<Messages>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(Messages::class.java)
                    if (message != null &&
                        ((message.senderId == userId && message.receiverId == receiverId) ||
                                (message.senderId == receiverId && message.receiverId == userId))) {
                        messageList.add(message)
                    }
                }
                onMessagesReceived(messageList)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("FirebaseHelper", "Error reading messages from Firebase: ${error.message}")
            }
        })
    }

    suspend fun getUsernameFromDatabase(userId: String): String? {
        return suspendCoroutine { continuation ->
            val usernameRef = database.child("Users").child(userId).child("Username")

            usernameRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val username = dataSnapshot.value as? String
                    continuation.resume(username)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    continuation.resume(null)
                }
            })
        }
    }

}

