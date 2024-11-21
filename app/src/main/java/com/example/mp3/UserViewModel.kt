package com.example.mp3

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel(private val placesClient: PlacesClient) : ViewModel() {
    // State declarations
    private val _locationQuery = MutableStateFlow("")
    val locationQuery: StateFlow<String> = _locationQuery

    private val _autocompleteSuggestions = MutableStateFlow<List<String>>(emptyList())
    val autocompleteSuggestions: StateFlow<List<String>> = _autocompleteSuggestions

    private val _selectedLocation = MutableStateFlow<String?>(null)
    val selectedLocation: StateFlow<String?> = _selectedLocation

    private val _selectedLocationLatLng = MutableStateFlow<LatLng?>(null)
    val selectedLocationLatLng: StateFlow<LatLng?> = _selectedLocationLatLng

    private val _isFilterExpanded = MutableStateFlow(false)
    val isFilterExpanded: StateFlow<Boolean> = _isFilterExpanded

    private val _budget = MutableStateFlow(0f)
    val budget: StateFlow<Float> = _budget

    private val _isPGSelected = MutableStateFlow(false)
    val isPGSelected: StateFlow<Boolean> = _isPGSelected

    private val _isRentalSelected = MutableStateFlow(false)
    val isRentalSelected: StateFlow<Boolean> = _isRentalSelected

    private val _isHostelSelected = MutableStateFlow(false)
    val isHostelSelected: StateFlow<Boolean> = _isHostelSelected

    // Functions
    fun onLocationQueryChanged(query: String) {
        _locationQuery.value = query
        if (_selectedLocation.value != null) {
            _selectedLocation.value = null
        }
        searchLocation(query)
    }

    fun onLocationSelected(location: String) {
        _selectedLocation.value = location
        _locationQuery.value = location
        _autocompleteSuggestions.value = emptyList()
        getLocationCoordinates(location)
    }

    fun toggleFilters() {
        _isFilterExpanded.value = !_isFilterExpanded.value
    }

    fun onBudgetChanged(value: Float) {
        _budget.value = value
    }

    fun onPGSelectionChanged(selected: Boolean) {
        _isPGSelected.value = selected
    }

    fun onRentalSelectionChanged(selected: Boolean) {
        _isRentalSelected.value = selected
    }

    fun onHostelSelectionChanged(selected: Boolean) {
        _isHostelSelected.value = selected
    }

    private fun searchLocation(query: String) {
        if (query.isEmpty()) {
            _autocompleteSuggestions.value = emptyList()
            return
        }

        try {
            val request = FindAutocompletePredictionsRequest.builder()
                .setQuery(query)
                .setSessionToken(AutocompleteSessionToken.newInstance())
                .build()

            placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener { response ->
                    _autocompleteSuggestions.value = response.autocompletePredictions.map {
                        it.getFullText(null).toString()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Places API", "Error fetching autocomplete predictions", exception)
                    _autocompleteSuggestions.value = emptyList()
                }
        } catch (e: Exception) {
            Log.e("Places API", "Error in searchLocation", e)
            _autocompleteSuggestions.value = emptyList()
        }
    }

    private fun getLocationCoordinates(location: String) {
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(location)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                if (response.autocompletePredictions.isNotEmpty()) {
                    val placeId = response.autocompletePredictions[0].placeId
                    val placeFields = listOf(Place.Field.LAT_LNG)
                    val fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build()

                    placesClient.fetchPlace(fetchPlaceRequest)
                        .addOnSuccessListener { fetchPlaceResponse ->
                            fetchPlaceResponse.place.latLng?.let { latLng ->
                                _selectedLocationLatLng.value = latLng
                            }
                        }
                }
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