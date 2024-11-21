package com.example.mp3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(private val placesClient: PlacesClient) : ViewModel() {
    // Location query
    private val _locationQuery = MutableStateFlow("")
    val locationQuery: StateFlow<String> = _locationQuery

    // Autocomplete suggestions
    private val _autocompleteSuggestions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val autocompleteSuggestions: StateFlow<List<AutocompletePrediction>> = _autocompleteSuggestions

    // Budget range
    private val _budgetRange = MutableStateFlow(0f..100000f)
    val budgetRange: StateFlow<ClosedFloatingPointRange<Float>> = _budgetRange

    // Property type
    private val _propertyType = MutableStateFlow("Rental")
    val propertyType: StateFlow<String> = _propertyType

    // Location query handling
    fun onLocationQueryChanged(query: String) {
        _locationQuery.value = query
        searchLocation(query)
    }

    fun onLocationSelected(location: String) {
        _locationQuery.value = location
    }

    // Budget range handling
    fun onBudgetRangeChanged(start: Float, end: Float) {
        _budgetRange.value = start..end
    }

    // Property type handling
    fun onPropertyTypeChanged(type: String) {
        _propertyType.value = type
    }

    // Places API search
    private fun searchLocation(query: String) {
        if (query.isEmpty()) {
            _autocompleteSuggestions.value = emptyList()
            return
        }

        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .setSessionToken(token)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                _autocompleteSuggestions.value = response.autocompletePredictions
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
                _autocompleteSuggestions.value = emptyList()
            }
    }
}

class UserViewModelFactory(private val placesClient: PlacesClient) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(placesClient) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}