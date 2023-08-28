package com.example.chats.screens

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chats.R
import com.example.chats.data.Messages
import com.example.chats.navigation.NavigationDestinations
import com.google.firebase.auth.FirebaseAuth

object UserMessageScreen : NavigationDestinations{

    override val route: String = "UserChats"
    override val title: String = "UserChats"
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingScreen(
    title:String,    navigateBack: () -> Unit,
    messagingViewModel: UserChatsViewModel,
    canNavigateBack: Boolean = true,
    userId: String,
    receiverUsername:String,
) {

    var currentUserID by remember { mutableStateOf("") }
    var firebaseUser = FirebaseAuth.getInstance().currentUser?.uid

    currentUserID = firebaseUser.toString()

    val loadedUsernameState = remember(userId) {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(userId) {
        val username = messagingViewModel.loadUsernameForCurrentUser(currentUserID)
        Log.e("Loaded Username","$username")
        loadedUsernameState.value = username
    }

    val loadedUsername = loadedUsernameState.value


    Scaffold(topBar = {
        MessagingTopAppBar(
            userId = receiverUsername,
            title = title,
            canNavigateBack = canNavigateBack,
            navigateBack
        )
    },
        bottomBar = {
            MessagingBottomAppBar(
                senderId = currentUserID,
                receiverId = userId,
                messagingViewModel
            )
        })
    { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (loadedUsername != null) {
                MessagingBody(
                    senderId = currentUserID,
                    receiverId = userId,
                    messagingViewModel = messagingViewModel,
                    senderUsername = loadedUsername,
                    receiverUsername = receiverUsername
                )
            }
        }
    }

}
@Composable
fun MessagingBody(senderId: String,receiverId: String,messagingViewModel: UserChatsViewModel,senderUsername: String,receiverUsername: String) {
    val messagesState = remember { mutableStateListOf<Messages>() }

    messagingViewModel.listenForMessages(senderId,receiverId) { messages ->
        messagesState.clear()
        messagesState.addAll(messages)
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(messagesState) { message ->
            val isSentByCurrentUser = message.senderId == senderId
            MessageCard(
                senderUsername = senderUsername,
                receiverUsername = receiverUsername,
                message = message.messageText,
                isCurrentUser = isSentByCurrentUser
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagingTopAppBar(
    userId: String,
    title: String,
    canNavigateBack: Boolean,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        title = { Title(userId = userId, title = title ) },
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navigateBack() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        }
    )
}
@Composable
fun Title(userId: String,title: String) {
    Column {
        Text(text = title)

        Spacer(modifier = Modifier.padding(5.dp))

        Text(text = userId)
    }
}

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun UserSend(senderId: String, receiverId: String, messagingViewModel: UserChatsViewModel) {
        val messageText = messagingViewModel.uiMessage.collectAsState().value

        Row(
            modifier = Modifier
                .fillMaxSize()
            
        ) {
            // Display messages here

            TextField(
                value = messageText.message,
                onValueChange = { messagingViewModel.updateMessage(it) },
                placeholder = { Text(text = "Message here") }
            )
            
            Spacer(modifier = Modifier.padding(3.dp))

            IconButton(
                onClick = {
                    messagingViewModel.sendMessage(
                        senderId = senderId,
                        receiverId = receiverId,
                        messageText = messageText.message,
                        onComplete = {
                            // Handle success, e.g., clear the input field

                        },

                        )
                },
                modifier = Modifier,
                content = { Icon(painter = painterResource(id =R.drawable.baseline_send_24 ), contentDescription = "Send Button")}

            )

        }
    }

@Composable
fun MessageCard(senderUsername: String, receiverUsername: String, message: String, isCurrentUser: Boolean) {
    val horizontalAlignment = if (isCurrentUser) Alignment.BottomEnd else Alignment.BottomStart
  
    val color by animateColorAsState(
        targetValue = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.primaryContainer,
    )
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),

            contentAlignment = horizontalAlignment
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.5f) // Limit the width to half of the screen
                .background(color = color)
                .padding(8.dp)
                .clip(RoundedCornerShape(4.dp)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = color)

            ) {
                val usernameToShow = if (isCurrentUser) senderUsername else receiverUsername
                Text(
                    text = usernameToShow,
                    fontWeight = FontWeight.Bold,
                    color = Color.Cyan,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message,
                    fontSize = 14.sp
                )
            }
        }
    }
}





@Composable
    fun MessagingBottomAppBar(
        senderId: String, receiverId: String, messagingViewModel: UserChatsViewModel,
        modifier: Modifier = Modifier,
    ) {
        BottomAppBar(
            content = {
                UserSend(
                    senderId = senderId,
                    receiverId = receiverId,
                    messagingViewModel
                )
            },
            modifier = modifier
        )

    }

@Preview
@Composable
fun UserSend() {
    MessagingBottomAppBar(senderId = "", receiverId = "" , messagingViewModel = UserChatsViewModel())
}
