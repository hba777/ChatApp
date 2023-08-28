package com.example.chats.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chats.navigation.NavigationDestinations
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

object RegistrationScreen : NavigationDestinations {
    override val route: String = "Registration"

    override val title: String = "Registration Screen"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    title: String,
    navigateToHomeScreen: () -> Unit,
    navigateToChats: () -> Unit,
    modifier : Modifier = Modifier) {

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            RegistrationTopAppBar(
                title = title,
                canNavigateBack = false,
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->

        RegistrationBody(
            navigateToHomeScreen=navigateToHomeScreen,
            navigateToChats=navigateToChats,
            snackbarHostState=snackbarHostState,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize())
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationBody(navigateToHomeScreen: () -> Unit,navigateToChats: () -> Unit,modifier: Modifier,snackbarHostState: SnackbarHostState,viewModel: RegistrationViewModel = viewModel()) {
    val uiStateQuery = viewModel.uiStateSearch.collectAsState().value

    val user = viewModel.auth.currentUser
    var userID by remember { mutableStateOf("") }
    var firebaseUser = FirebaseAuth.getInstance().currentUser?.uid
    val coroutineScope = rememberCoroutineScope()
    userID =firebaseUser.toString()

    var signInFailed by remember { mutableStateOf(false) } // Define the signInFailed variable

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (user == null) {
            // User is not authenticated
            OutlinedTextField(
                value = uiStateQuery.email,
                onValueChange = { viewModel.updateEmail(it) },
                placeholder = { Text(text = "Enter Email ") },
                modifier = Modifier.padding(16.dp)
            )
            Text(text = "Email must be a valid otherwise signup wont work",
                modifier = Modifier.padding(16.dp)
            )

            OutlinedTextField(
                value = uiStateQuery.password,
                onValueChange = { viewModel.updatePassword(it) },
                placeholder = { Text(text = "Enter Password ") },
                modifier = Modifier.padding(16.dp)
            )
            Text(text = "Password must be greater than 4 characters",
                modifier = Modifier.padding(16.dp)
            )

            Button(
                onClick = {
                    if (!uiStateQuery.email.isNullOrBlank() && !uiStateQuery.password.isNullOrBlank()) {
                        viewModel.signIn(uiStateQuery.email, uiStateQuery.password) { success ->
                            if (success) {
                                navigateToChats()
                            } else {
                                // Handle sign-in failure
                                signInFailed = true
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Sign-in failed. Please check your credentials and try again.")

                                }

                            }
                        }
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Sign In")
            }

            Button(
                onClick = { if(!uiStateQuery.email.isNullOrBlank() && !uiStateQuery.password.isNullOrBlank())
                {
                    viewModel.signUp(uiStateQuery.email, uiStateQuery.password)
                    navigateToHomeScreen()
                }

                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Sign Up")
            }

        } else {
            OutlinedTextField(
                value = uiStateQuery.email,
                onValueChange = { viewModel.updateEmail(it) },
                placeholder = { Text(text = "Enter Email ") },
                modifier = Modifier.padding(16.dp),
            )
            Text(text = "Email must be a valid otherwise signup wont work",
                modifier = Modifier.padding(16.dp)
            )


            OutlinedTextField(
                value = uiStateQuery.password,
                onValueChange = { viewModel.updatePassword(it) },
                placeholder = { Text(text = "Enter Password ") },
                modifier = Modifier.padding(16.dp)
            )
            Text(text = "Password must be greater than 4 characters",
                modifier = Modifier.padding(16.dp)
            )

            Button(
                onClick = {
                    if (!uiStateQuery.email.isNullOrBlank() && !uiStateQuery.password.isNullOrBlank()) {
                        viewModel.signIn(uiStateQuery.email, uiStateQuery.password) { success ->
                            if (success) {
                                navigateToChats()
                            } else {
                                // Handle sign-in failure
                                signInFailed = true
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Sign-in failed. Please check your credentials and try again.")

                                }
                            }
                        }
                    }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Sign In")
            }

            Button(
                onClick = { if(!uiStateQuery.email.isNullOrBlank() && !uiStateQuery.password.isNullOrBlank())
                {
                    viewModel.signUp(uiStateQuery.email, uiStateQuery.password)
                    navigateToHomeScreen()
                }
                },
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Sign Up")
            }
        }

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationTopAppBar(
    title: String,
    canNavigateBack: Boolean,
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
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null
                    )
                }
            }
        }
    )
}






