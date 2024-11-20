package com.example.mp3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.net.PlacesClient

class UserViewModelFactory(private val placesClient: PlacesClient) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(placesClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

