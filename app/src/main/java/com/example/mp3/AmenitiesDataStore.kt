package com.example.mp3

object AmenitiesDataStore {
    private var amenitiesData: HashMap<String, List<String>>? = null

    fun saveAmenitiesData(data: HashMap<String, List<String>>) {
        amenitiesData = data
    }

    fun getAmenitiesData(): HashMap<String, List<String>> {
        return amenitiesData ?: hashMapOf()
    }

    fun clearAmenitiesData() {
        amenitiesData = null
    }
}