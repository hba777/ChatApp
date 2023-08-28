package com.example.chats.screens

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.chats.navigation.NavigationDestinations

object HomeScreen : NavigationDestinations {
    override val route: String = "Home"
    override val title: String = "HomeScreen"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    title: String,
    viewModel: RegistrationViewModel,
    navigateToChats:() -> Unit,
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateBack: Boolean = true,
) {

    Scaffold(topBar = { HomeTopAppBar(title = title, canNavigateBack = true,navigateBack=navigateBack ) }) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            HomeBody(viewModel=viewModel,navigateBack,navigateToChats)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    navigateUp: () -> Unit = {}
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeBody(viewModel:RegistrationViewModel,navigateBack: () -> Unit,navigateToChats: () -> Unit
) {

    val uiStateQuery = viewModel.uiStateSearch.collectAsState().value
    val user = viewModel.auth.currentUser
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val galleryLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uriList ->
            // process  the received image uri
            selectedImageUri=uriList
        }

    OutlinedTextField(
        value = uiStateQuery.email,
        onValueChange = { viewModel.updateEmail(it) },
        placeholder = { Text(text = "Confirm Email ") },
        modifier = Modifier.padding(16.dp)
    )

    OutlinedTextField(
        value = uiStateQuery.username,
        onValueChange = { viewModel.updateUsername(it) },
        placeholder = { Text(text = "Enter Username ") },
        modifier = Modifier.padding(16.dp)
    )

    OutlinedTextField(
        value = uiStateQuery.password,
        onValueChange = { viewModel.updatePassword(it) },
        placeholder = { Text(text = "Confirm Password ") },
        modifier = Modifier.padding(16.dp)
    )

    Button(
        onClick = {
            // Launch the image picker
            galleryLauncher.launch("image/*")
            viewModel.updateProfileUrl(selectedImageUri.toString())

        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Select Image")
    }

    Button(
        onClick = {
            if (user != null && !uiStateQuery.email.isNullOrBlank() && !uiStateQuery.password.isNullOrBlank()) {
                viewModel.writeToDatabase(user.uid, uiStateQuery.email,uiStateQuery.username,uiStateQuery.password,selectedImageUri.toString())
                Log.d("ProfileUrl","${uiStateQuery.profileUrl}")
                navigateToChats()
            }
        },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Confirm Details")
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = { viewModel.signOut()
            navigateBack()
                  },
        modifier = Modifier.padding(16.dp)
    ) {
        Text("Sign Out")
    }
}
