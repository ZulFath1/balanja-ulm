package com.example.balanja

import com.example.balanja.data.repository.*
import com.example.balanja.domain.repository.*
import com.example.balanja.domain.usecase.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object AppContainer {
    val firebaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firebaseDatabase: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().reference
    }

    val stallRepository: StallRepository by lazy { StallRepositoryImpl() }
    val reviewRepository: ReviewRepository by lazy { ReviewRepositoryImpl() }
    val authRepository: AuthRepository by lazy { AuthRepositoryImpl() }
    val weatherRepository: WeatherRepository by lazy { WeatherRepositoryImpl() }
    val favoriteRepository: FavoriteRepository by lazy { FavoriteRepositoryImpl() }

    val getStallsUseCase by lazy { GetStallsUseCase(stallRepository) }
    val getCampusWeatherUseCase by lazy { GetCampusWeatherUseCase(weatherRepository) }
    val addFavoriteUseCase by lazy { AddFavoriteUseCase(favoriteRepository) }
    val getFavoritesUseCase by lazy { GetFavoritesUseCase(favoriteRepository) }
    val deleteFavoriteUseCase by lazy { DeleteFavoriteUseCase(favoriteRepository) }
}
