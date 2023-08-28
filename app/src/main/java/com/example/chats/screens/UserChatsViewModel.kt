package com.example.chats.screens

import androidx.lifecycle.ViewModel
import com.example.chats.data.FirebaseHelper
import com.example.chats.data.FirebaseHelper.getUsernameFromDatabase
import com.example.chats.data.Messages
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserChatsViewModel : ViewModel() {

    private val _uimessage = MutableStateFlow(messageUiState())
    val uiMessage = _uimessage.asStateFlow()




    private val _messages = MutableStateFlow<List<Messages>>(emptyList())
    val messages: StateFlow<List<Messages>> = _messages


    fun updateMessage(message: String){
        _uimessage.update { currentState ->
            currentState.copy(
                message = message
            )
        }
    }


    fun sendMessage(
        senderId: String,
        receiverId: String,
        messageText: String,
        onComplete: (Boolean) -> Unit
    ) {
        FirebaseHelper.sendMessage(senderId, receiverId, messageText, onComplete)
    }

    fun listenForMessages(userId: String,receiverId: String, onMessagesReceived: (List<Messages>) -> Unit) {
        FirebaseHelper.readMessagesBetweenUsers(userId,receiverId, onMessagesReceived)
    }

    // Function to load the username based on the current user
    suspend fun loadUsernameForCurrentUser(userId: String): String? {
        return getUsernameFromDatabase(userId)
    }

    private val firebaseAuth = FirebaseAuth.getInstance()

    // ... Your other functions

    suspend fun loadCurrentUsername(): String? {
        val currentUser = firebaseAuth.currentUser
        val userId = currentUser?.uid
        if (userId != null) {
            return getUsernameFromDatabase(userId)
        }
        return null
    }
}

data class messageUiState(
    val message: String="",
)

