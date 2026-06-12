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
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import com.example.balanja.AppContainer
import com.example.balanja.domain.usecase.SignInUseCase
import com.example.balanja.presentation.auth.AuthViewModel
import com.example.balanja.presentation.auth.LoginScreen
import com.example.balanja.presentation.review.WriteReviewScreen
import com.example.balanja.presentation.review.WriteReviewViewModel
import com.example.balanja.presentation.review.MyReviewsScreen
import com.example.balanja.presentation.review.MyReviewsViewModel
import com.example.balanja.presentation.search.AddStallScreen
import com.example.balanja.presentation.map.MapScreen
import com.example.balanja.presentation.map.MapViewModel
import com.example.balanja.presentation.profile.ProfileScreen
import com.example.balanja.presentation.profile.ProfileViewModel
import com.example.balanja.presentation.favorite.FavoriteStallsScreen
import com.example.balanja.presentation.favorite.FavoriteStallsViewModel
import com.example.balanja.presentation.detail.StallDetailScreen
import com.example.balanja.presentation.detail.StallDetailViewModel

// Factory untuk inject SignInUseCase ke AuthViewModel
class AuthViewModelFactory(
    private val signInUseCase: SignInUseCase,
    private val signInWithGoogleUseCase: com.example.balanja.domain.usecase.SignInWithGoogleUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AuthViewModel(signInUseCase, signInWithGoogleUseCase) as T
    }
}

val screensWithoutBottomNav = listOf(
    Screen.Login.route,
    Screen.Register.route,
    Screen.StallDetail.route,
    Screen.CommunityReview.route,
    Screen.WriteReview.route,
    Screen.MyReviews.route,
    Screen.Map.route
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // showBottomBar no longer needed since navbar is rendered per route

    val startDestination = androidx.compose.runtime.remember {
        if (AppContainer.authRepository.isLoggedIn()) Screen.Home.route else Screen.Login.route
    }

    fun isNavbarRoute(route: String?): Boolean {
        if (route == null) return false
        return screensWithoutBottomNav.none { pattern ->
            route.startsWith(pattern.substringBefore("{"))
        }
    }

    Scaffold(
        containerColor = Color(0xFFFBF9F8)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            enterTransition = {
                if (isNavbarRoute(targetState.destination.route)) {
                    fadeIn(animationSpec = tween(300))
                } else {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(300)
                    )
                }
            },
            exitTransition = {
                fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                if (isNavbarRoute(targetState.destination.route)) {
                    fadeIn(animationSpec = tween(300))
                } else {
                    slideInHorizontally(
                        initialOffsetX = { fullWidth -> -fullWidth },
                        animationSpec = tween(300)
                    )
                }
            },
            popExitTransition = {
                if (isNavbarRoute(initialState.destination.route)) {
                    fadeOut(animationSpec = tween(300))
                } else {
                    slideOutHorizontally(
                        targetOffsetX = { fullWidth -> fullWidth },
                        animationSpec = tween(300)
                    )
                }
            }
        ) {
            composable(Screen.Login.route) {
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory(AppContainer.signInUseCase, AppContainer.signInWithGoogleUseCase)
                )
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.Register.route)
                    }
                )
            }

            composable(Screen.Register.route) {
                val registerViewModel: com.example.balanja.presentation.auth.RegisterViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return com.example.balanja.presentation.auth.RegisterViewModel(AppContainer.signUpUseCase) as T
                        }
                    }
                )
                com.example.balanja.presentation.auth.RegisterScreen(
                    viewModel = registerViewModel,
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            composable(Screen.Home.route) {
                val viewModel: com.example.balanja.presentation.home.HomeViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return com.example.balanja.presentation.home.HomeViewModel(
                                AppContainer.getAllStallsUseCase,
                                AppContainer.getCampusWeatherUseCase,
                                AppContainer.getFavoritesUseCase,
                                AppContainer.addFavoriteUseCase,
                                AppContainer.deleteFavoriteUseCase,
                                AppContainer.authRepository
                            ) as T
                        }
                    }
                )
                Box(modifier = Modifier.fillMaxSize()) {
                    com.example.balanja.presentation.home.HomeScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { stallId ->
                            navController.navigate(Screen.StallDetail.createRoute(stallId))
                        }
                    )
                    BalanjaBottomNav(navController = navController, modifier = Modifier.align(Alignment.BottomCenter))
                }
            }
            composable(Screen.Search.route) {
                val viewModel: com.example.balanja.presentation.search.SearchViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return com.example.balanja.presentation.search.SearchViewModel(
                                AppContainer.getAllStallsUseCase,
                                AppContainer.getRecentSearchesUseCase,
                                AppContainer.addRecentSearchUseCase,
                                AppContainer.clearRecentSearchesUseCase
                            ) as T
                        }
                    }
                )
                Box(modifier = Modifier.fillMaxSize()) {
                    com.example.balanja.presentation.search.SearchScreen(
                        viewModel = viewModel,
                        onNavigateToDetail = { stallId ->
                            navController.navigate(Screen.StallDetail.createRoute(stallId))
                        }
                    )
                    BalanjaBottomNav(navController = navController, modifier = Modifier.align(Alignment.BottomCenter))
                }
            }
            composable(
                route = Screen.Map.route,
                arguments = listOf(navArgument("stallId") { type = NavType.StringType })
            ) { backStackEntry ->
                val stallId = backStackEntry.arguments?.getString("stallId") ?: ""
                val viewModel: MapViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return MapViewModel(stallId, AppContainer.getAllStallsUseCase) as T
                        }
                    }
                )
                MapScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.AddStall.route) {
                val viewModel: com.example.balanja.presentation.search.AddStallViewModel = viewModel()
                Box(modifier = Modifier.fillMaxSize()) {
                    AddStallScreen(
                        viewModel = viewModel
                    )
                    BalanjaBottomNav(navController = navController, modifier = Modifier.align(Alignment.BottomCenter))
                }
            }
            composable(Screen.Profile.route) {
                val viewModel: ProfileViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return ProfileViewModel(AppContainer.authRepository, AppContainer.stallRepository) as T
                        }
                    }
                )
                Box(modifier = Modifier.fillMaxSize()) {
                    ProfileScreen(navController, viewModel)
                    BalanjaBottomNav(navController = navController, modifier = Modifier.align(Alignment.BottomCenter))
                }
            }
            composable(Screen.Favorites.route) {
                val viewModel: FavoriteStallsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return FavoriteStallsViewModel(AppContainer.getFavoritesUseCase, AppContainer.deleteFavoriteUseCase) as T
                        }
                    }
                )
                FavoriteStallsScreen(navController, viewModel)
            }

            composable(
                route = "stall_detail/{stallId}",
                arguments = listOf(navArgument("stallId") { type = NavType.StringType })
            ) { backStackEntry ->
                val stallId = backStackEntry.arguments?.getString("stallId") ?: ""
                val viewModel: StallDetailViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return StallDetailViewModel(
                                AppContainer.getStallDetailUseCase,
                                AppContainer.toggleStallStatusUseCase,
                                AppContainer.getReviewsUseCase,
                                AppContainer.isFavoriteUseCase,
                                AppContainer.addFavoriteUseCase,
                                AppContainer.deleteFavoriteUseCase,
                                AppContainer.authRepository
                            ) as T
                        }
                    }
                )
                StallDetailScreen(
                    viewModel = viewModel,
                    stallId = stallId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToReview = { id -> navController.navigate(Screen.WriteReview.createRoute(id)) },
                    onNavigateToMap = { id -> navController.navigate(Screen.Map.createRoute(id)) },
                    onNavigateToCommunityReview = { id -> navController.navigate(Screen.CommunityReview.createRoute(id)) }
                )
            }
            composable(
                route = "community_review/{stallId}",
                arguments = listOf(navArgument("stallId") { type = NavType.StringType })
            ) { backStackEntry ->
                val stallId = backStackEntry.arguments?.getString("stallId") ?: ""
                val viewModel: com.example.balanja.presentation.review.CommunityReviewViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return com.example.balanja.presentation.review.CommunityReviewViewModel(
                                AppContainer.getReviewsUseCase
                            ) as T
                        }
                    }
                )
                com.example.balanja.presentation.review.CommunityReviewScreen(
                    viewModel = viewModel,
                    stallId = stallId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "write_review/{stallId}?reviewId={reviewId}",
                arguments = listOf(
                    navArgument("stallId")  { type = NavType.StringType },
                    navArgument("reviewId") { type = NavType.StringType; nullable = true; defaultValue = null }
                )
            ) { backStackEntry ->
                val stallId  = backStackEntry.arguments?.getString("stallId") ?: ""
                val reviewId = backStackEntry.arguments?.getString("reviewId")
                val viewModel: WriteReviewViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return WriteReviewViewModel(
                                stallId = stallId,
                                reviewId = reviewId,
                                addReviewUseCase = AppContainer.addReviewUseCase,
                                editReviewUseCase = AppContainer.editReviewUseCase,
                                getReviewsUseCase = AppContainer.getReviewsUseCase,
                                recalculateStallRatingUseCase = AppContainer.recalculateStallRatingUseCase,
                                authRepository = AppContainer.authRepository,
                                cloudinaryApiService = AppContainer.cloudinaryApiService
                            ) as T
                        }
                    }
                )
                WriteReviewScreen(navController, stallId, reviewId, viewModel)
            }
            composable(Screen.MyReviews.route) {
                val viewModel: MyReviewsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return MyReviewsViewModel(
                                getMyReviewsUseCase = AppContainer.getMyReviewsUseCase,
                                deleteReviewUseCase = AppContainer.deleteReviewUseCase,
                                recalculateStallRatingUseCase = AppContainer.recalculateStallRatingUseCase,
                                authRepository = AppContainer.authRepository
                            ) as T
                        }
                    }
                )
                MyReviewsScreen(navController, viewModel)
            }
            composable("my_stalls") {
                val viewModel: com.example.balanja.presentation.profile.MyStallsViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            @Suppress("UNCHECKED_CAST")
                            return com.example.balanja.presentation.profile.MyStallsViewModel(
                                authRepository = AppContainer.authRepository,
                                stallRepository = AppContainer.stallRepository
                            ) as T
                        }
                    }
                )
                com.example.balanja.presentation.profile.MyStallsScreen(navController, viewModel)
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