package com.example.balanja.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.balanja.AppContainer
import com.example.balanja.domain.usecase.SignInUseCase
import com.example.balanja.presentation.auth.AuthViewModel
import com.example.balanja.presentation.auth.LoginScreen
//import com.example.balanja.presentation.review.WriteReviewScreen
import com.example.balanja.presentation.search.AddStallScreen

// Factory untuk inject SignInUseCase ke AuthViewModel
class AuthViewModelFactory(
    private val signInUseCase: SignInUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AuthViewModel(signInUseCase) as T
    }
}

val screensWithoutBottomNav = listOf(
    Screen.Login.route,
    Screen.StallDetail.route,
    Screen.CommunityReview.route,
    Screen.WriteReview.route,
    Screen.MyReviews.route,
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = screensWithoutBottomNav.none { pattern ->
        currentRoute?.startsWith(pattern.substringBefore("{")) == true
    }

    Scaffold(
        containerColor = Color(0xFFFBF9F8),
        bottomBar = {
            if (showBottomBar) BalanjaBottomNav(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(AppContainer.signInUseCase)
                )
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Home.route) {
                PlaceholderScreen("Home Screen")
            }
            composable(Screen.Search.route) {
                PlaceholderScreen("Search Screen")
            }
            composable(Screen.AddStall.route) {
                AddStallScreen()
            }
            composable(Screen.Profile.route) {
                PlaceholderScreen("Profile Screen")
            }
            composable(Screen.Favorites.route) {
                PlaceholderScreen("Favorites Screen")
            }

            composable(
                route = "stall_detail/{stallId}",
                arguments = listOf(navArgument("stallId") { type = NavType.StringType })
            ) { backStackEntry ->
                val stallId = backStackEntry.arguments?.getString("stallId") ?: ""
                PlaceholderScreen("Stall Detail — $stallId")
            }
            composable(
                route = "community_review/{stallId}",
                arguments = listOf(navArgument("stallId") { type = NavType.StringType })
            ) { backStackEntry ->
                val stallId = backStackEntry.arguments?.getString("stallId") ?: ""
                PlaceholderScreen("Community Review — $stallId")
            }
//            composable(
//                route = "write_review/{stallId}?reviewId={reviewId}",
//                arguments = listOf(
//                    navArgument("stallId")  { type = NavType.StringType },
//                    navArgument("reviewId") { type = NavType.StringType; nullable = true; defaultValue = null }
//                )
//            ) { backStackEntry ->
//                val stallId  = backStackEntry.arguments?.getString("stallId") ?: ""
//                val reviewId = backStackEntry.arguments?.getString("reviewId")
//                WriteReviewScreen(navController, stallId, reviewId)
//            }
            composable(Screen.MyReviews.route) {
                PlaceholderScreen("My Reviews Screen")
            }
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFFBF9F8)),
        contentAlignment = Alignment.Center
    ) {
        Text(name, style = MaterialTheme.typography.titleMedium)
    }
}