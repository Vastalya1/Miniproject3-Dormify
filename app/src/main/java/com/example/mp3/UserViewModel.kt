package com.example.mp3

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserViewModel(private val placesClient: PlacesClient) : ViewModel() {

    private val _locationQuery = MutableStateFlow("")
    val locationQuery: StateFlow<String> get() = _locationQuery

    private val _autocompleteSuggestions = MutableStateFlow(emptyList<AutocompletePrediction>())
    val autocompleteSuggestions: StateFlow<List<AutocompletePrediction>> get() = _autocompleteSuggestions

    private val _budgetRange = MutableStateFlow(0f..100000f)
    val budgetRange: StateFlow<ClosedFloatingPointRange<Float>> get() = _budgetRange

    private val _propertyType = MutableStateFlow("Rental")
    val propertyType: StateFlow<String> get() = _propertyType

    fun onLocationQueryChanged(query: String) {
        _locationQuery.value = query
        viewModelScope.launch {
            try {
                val predictions = getAutocompletePredictions(query)
                _autocompleteSuggestions.value = predictions
            } catch (e: Exception) {
                // Handle error appropriately
                _autocompleteSuggestions.value = emptyList()
            }
        }
    }

    private suspend fun getAutocompletePredictions(query: String): List<AutocompletePrediction> =
        suspendCancellableCoroutine { continuation ->
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    continuation.resume(response.autocompletePredictions)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    fun onLocationSelected(location: String) {
        _locationQuery.value = location
        _autocompleteSuggestions.value = emptyList()
    }

    fun onBudgetRangeChanged(start: Float, end: Float) {
        _budgetRange.value = start..end
    }

    fun onPropertyTypeChanged(type: String) {
        _propertyType.value = type
    }
}


