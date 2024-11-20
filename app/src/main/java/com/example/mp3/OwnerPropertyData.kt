package com.example.mp3

data class DataModel (
    val propertyType: String,
    val bhk: String,
    val buildUpArea: String,
    val furnishType: String,
    val amenities: List<String>,  // List of selected amenities
    val location: Location,      // New field to store location (Address, City)
    val monthlyRent: String,
    val availableFrom: String,
    val securityDeposit: String,
    val customSecurityDepositAmount: String? = null
    )

data class Location(
    val address: String,
    val city: String,
    val latitude: Double?,
    val longitude: Double?
)