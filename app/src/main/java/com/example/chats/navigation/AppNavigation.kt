package com.example.chats.navigation


import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chats.screens.ChatScreen
import com.example.chats.screens.ChatViewModel
import com.example.chats.screens.ChatsScreen
import com.example.chats.screens.HomeScreen
import com.example.chats.screens.MessagingScreen
import com.example.chats.screens.RegistrationScreen
import com.example.chats.screens.RegistrationViewModel
import com.example.chats.screens.UserChatsViewModel
import com.example.chats.screens.UserMessageScreen

/**
 * Top level composable that represents screens for the application.
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ChatApp(navController: NavHostController = rememberNavController()) {
    InventoryNavHost(navController = navController)
}
@Composable
fun InventoryNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = RegistrationScreen.route,
        modifier = modifier
    ) {
        composable(route = RegistrationScreen.route) {
            RegistrationScreen(
                title = RegistrationScreen.title,
                navigateToChats = { navController.navigate(ChatsScreen.route) },
                navigateToHomeScreen = { navController.navigate(HomeScreen.route) },
            )
        }
        composable(route = HomeScreen.route) {
            HomeScreen(
                title = HomeScreen.route,
                navigateToChats = { navController.navigate(ChatsScreen.route) },
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() },
                viewModel = RegistrationViewModel(),

                )
        }

        composable(route = ChatsScreen.route) {
            ChatScreen(
                title = ChatsScreen.title,
                viewModel = ChatViewModel(),
                navigateToChats = { userId, username -> // Pass both user ID and username
                    navController.navigate("${UserMessageScreen.route}/$userId/$username")
                },
                navigateBack = { navController.popBackStack() })
        }

        composable(route = "${UserMessageScreen.route}/{userId}/{username}") { // Update the route
                backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            val username = backStackEntry.arguments?.getString("username") // Extract username
            if (userId != null && username != null) {
                MessagingScreen(
                    title = UserMessageScreen.route,
                    userId = userId,
                    receiverUsername= username, // Pass the extracted username
                    messagingViewModel = UserChatsViewModel(),
                    navigateBack = { navController.navigate(ChatsScreen.route) }
                )
            }
        }
    }
}