package com.example.lostandfoundapp

data class Advert(
    val id: Int,
    val postType: String,
    val name: String,
    val phone: String,
    val description: String,
    val date: String,
    val location: String,
    val category: String,
    val imageUri: String,
    val createdAt: String,
    val latitude: Double,
    val longitude: Double
)