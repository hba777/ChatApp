package com.example.chats.screens

import androidx.lifecycle.ViewModel
import com.example.chats.data.FirebaseHelper
import com.example.chats.data.UserData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RegistrationViewModel : ViewModel() {
    val auth = FirebaseAuth.getInstance()

    private val _uiStateSearch = MutableStateFlow(RegistrationUIState())
    val uiStateSearch = _uiStateSearch.asStateFlow()

    fun writeToDatabase(userId: String,email: String,username: String,password: String,profileUrl: String) {
        FirebaseHelper.writeToDatabase(userId, email,username,password,profileUrl)
    }

    fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
    }

//    fun signIn(email: String, password: String) {
//        auth.signInWithEmailAndPassword(email, password)
//    }
fun signIn(email: String, password: String, onSignInComplete: (success: Boolean) -> Unit) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Sign-in success
                onSignInComplete(true)
            } else {
                // Sign-in failed
                onSignInComplete(false)
            }
        }
}


    fun signOut() {
        auth.signOut()
    }


    fun updateEmail(email: String){
        _uiStateSearch.update { currentState ->
            currentState.copy(
                email = email
            )
        }
    }

    fun updateUsername(username: String){
        _uiStateSearch.update { currentState ->
            currentState.copy(
                username = username
            )
        }
    }
    fun updatePassword(password: String){
        _uiStateSearch.update { currentState ->
            currentState.copy(
                password = password
            )
        }
    }
    fun updateProfileUrl(profileUrl: String){
        _uiStateSearch.update { currentState ->
            currentState.copy(
                profileUrl = profileUrl
            )
        }
    }


}

data class RegistrationUIState(
    val email: String= "",
    val username: String="",
    val password: String="",
    val profileUrl: String="",
    val userList: List<UserData> = emptyList()
)
