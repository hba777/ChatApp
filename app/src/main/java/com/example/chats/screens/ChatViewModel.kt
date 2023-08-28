package com.example.chats.screens

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chats.data.FirebaseHelper
import com.example.chats.data.UserData

class ChatViewModel : ViewModel() {
    private val _userDataList = MutableLiveData<List<UserData>>()
    val userDataList: LiveData<List<UserData>> = _userDataList

    init {
        Log.d("MyViewModel", "ViewModel init block is called")

        // Call the FirebaseHelper function to read data from the database
        FirebaseHelper.readFromDatabase { userList ->
            Log.d("MyViewModel", "Data fetched from Firebase: $userList")
            _userDataList.postValue(userList)
        }

    }
}