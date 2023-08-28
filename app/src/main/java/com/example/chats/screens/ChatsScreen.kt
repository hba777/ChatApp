package com.example.chats.screens

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.chats.R
import com.example.chats.data.UserData
import com.example.chats.navigation.NavigationDestinations
import com.google.firebase.auth.FirebaseAuth

object ChatsScreen : NavigationDestinations {
    override val route: String = "Chats"
    override val title: String = "Chats"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    title: String,
    viewModel: ChatViewModel = viewModel(),
    navigateToChats:(String,String) -> Unit,
    navigateBack: () -> Unit,
    canNavigateBack: Boolean = true,
) {
    var userID by remember { mutableStateOf("") }
    var firebaseUser = FirebaseAuth.getInstance().currentUser?.uid

    userID =firebaseUser.toString()
    Log.d("SenderId", "$userID")

    Scaffold(topBar = { ChatTopAppBar(title = title, canNavigateBack = canNavigateBack,navigateBack ) }) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            ChatBody(viewModel=viewModel,navigateToChats)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    CenterAlignedTopAppBar(
        title = { Text(title) },
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
fun ChatBody(viewModel: ChatViewModel,navigateToChats:(String,String) -> Unit)
{
    val userDataList by viewModel.userDataList.observeAsState(emptyList())

    LazyColumn {
        items(userDataList) { userData ->
            UserItem(userData,navigateToChats)
        }
    }
}

@Composable
fun UserItem(userData: UserData,navigateToChats:(String,String) -> Unit) {
    // Compose your UI for each user item here using the userData
    var expanded by remember { mutableStateOf(false) }

    val color by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colorScheme.tertiaryContainer
        else MaterialTheme.colorScheme.primaryContainer,
    )

    Card(modifier = Modifier
        .fillMaxWidth()
        .background(color = color)
        .clickable { navigateToChats(userData.userId,userData.username) }
        .size(70.dp)) {

        Log.e("UserID", "${userData.userId}")
        Log.e("Username", "${userData.username}")
        Log.e("Email", "${userData.email}")
        Log.e("Password", "${userData.password}")
        Log.e("Profileurl", "${userData.profileUrl}")

        Row(modifier = Modifier.fillMaxWidth()
            .background(color = color)
            .weight(1f)) {
            AsyncImage(model = ImageRequest.Builder(LocalContext.current)
                .data(R.drawable.kazuhoo)
                .crossfade(enable = true)
                .build(), contentDescription = "ProfileIcon",
                contentScale = ContentScale.Fit,
                placeholder = painterResource(id = R.drawable.kazuhoo),
                modifier = Modifier
                    .clip(CircleShape)
                    .size(108.dp, 108.dp))
            Text(text = userData.username)

        }

    }
}

@Preview
@Composable
fun ChatsScreen() {
    Card(modifier = Modifier
        .fillMaxWidth()
        .clickable { }
        .size(32.dp)) {
        Row {
            Image(painter = painterResource(id = R.drawable.kazuhoo), contentDescription = null, contentScale = ContentScale.Fit, modifier = Modifier
                .clip(
                    CircleShape
                )
            )//            AsyncImage(model = ImageRequest.Builder(LocalContext.current)
//                .data("userData.profileUrl")
//                .crossfade(true)
//                .build(), contentDescription = "ProfileIcon",
//                contentScale = ContentScale.Fit,
//                placeholder = painterResource(id = R.drawable.kazuhoo),
//                modifier = Modifier
//                    .clip(CircleShape)
//                    .size(108.dp, 108.dp))
            Text(text = "userData.username")


        }

    }
}