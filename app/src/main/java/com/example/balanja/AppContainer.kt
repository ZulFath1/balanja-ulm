package com.example.balanja

import com.example.balanja.data.api.WeatherApiService
import com.example.balanja.data.repository.*
import com.example.balanja.domain.repository.*
import com.example.balanja.domain.usecase.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppContainer {
    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firebaseDatabase: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference
    }


    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            )
            .build()
    }

    val weatherApiService: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(WeatherApiService.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }


    val authRepository: AuthRepository by lazy { AuthRepositoryImpl(firebaseAuth) }
    val stallRepository: StallRepository by lazy { StallRepositoryImpl() }
    val reviewRepository: ReviewRepository by lazy { ReviewRepositoryImpl() }
    val weatherRepository: WeatherRepository by lazy { WeatherRepositoryImpl(weatherApiService) }
    lateinit var favoriteRepository: FavoriteRepository
        private set

    fun init(context: android.content.Context) {
        val database = com.example.balanja.data.local.BalanjaLocalDatabase.getInstance(context)
        favoriteRepository = FavoriteRepositoryImpl(database.favoriteStallDao())
    }

    // ─── Use Cases ────────────────────────────────────────────────────────────

    val signInUseCase by lazy { SignInUseCase(authRepository) }
    val getAllStallsUseCase by lazy { GetAllStallsUseCase(stallRepository) }
    val getStallDetailUseCase by lazy { GetStallDetailUseCase(stallRepository) }
    val toggleStallStatusUseCase by lazy { ToggleStallStatusUseCase(stallRepository) }
    val getCampusWeatherUseCase by lazy { GetCampusWeatherUseCase(weatherRepository) }
    val addFavoriteUseCase by lazy { AddFavoriteUseCase(favoriteRepository) }
    val getFavoritesUseCase by lazy { GetFavoritesUseCase(favoriteRepository) }
    val deleteFavoriteUseCase by lazy { DeleteFavoriteUseCase(favoriteRepository) }
    
    val addReviewUseCase by lazy { AddReviewUseCase(reviewRepository) }
    val editReviewUseCase by lazy { EditReviewUseCase(reviewRepository) }
    val deleteReviewUseCase by lazy { DeleteReviewUseCase(reviewRepository) }
    val getReviewsUseCase by lazy { GetReviewsUseCase(reviewRepository) }
    val getMyReviewsUseCase by lazy { GetMyReviewsUseCase(reviewRepository) }
    val recalculateStallRatingUseCase by lazy { RecalculateStallRatingUseCase(stallRepository, reviewRepository) }
}