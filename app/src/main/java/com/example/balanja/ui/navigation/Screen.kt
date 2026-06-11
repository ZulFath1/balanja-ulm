package com.example.balanja.ui.navigation

sealed class Screen(val route: String) {
    // Layar tanpa bottom nav
    object Login : Screen("login")

    // Layar dengan bottom nav (4 tab utama)
    object Home      : Screen("home")
    object Search    : Screen("search")
    object AddStall  : Screen("add_stall")
    object Profile   : Screen("profile")

    // Layar Favorit
    object Favorites : Screen("favorites")

    // Layar detail (tanpa bottom nav)
    object StallDetail : Screen("stall_detail/{stallId}") {
        fun createRoute(stallId: String) = "stall_detail/$stallId"
    }
    object CommunityReview : Screen("community_review/{stallId}") {
        fun createRoute(stallId: String) = "community_review/$stallId"
    }
    object WriteReview : Screen("write_review/{stallId}?reviewId={reviewId}") {
        fun createRoute(stallId: String, reviewId: String? = null) =
            if (reviewId != null) "write_review/$stallId?reviewId=$reviewId"
            else "write_review/$stallId"
    }
    object MyReviews : Screen("my_reviews")

    // Peta khusus untuk detail warung
    object Map : Screen("map/{stallId}") {
        fun createRoute(stallId: String) = "map/$stallId"
    }
}
