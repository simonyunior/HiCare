package com.example.hicare.data

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class RegisterResponse(
    val status: String,
    val message: String,
    val data: Data?
)

data class Data(
    val Username: String,
    val Email: String,
    val createdAt: String
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val status: String,
    val message: String,
)

data class PredictRequest(
    val username: String,
    val food_item: String
)

data class PredictResponse(
    val food_item: String,
    val calories: Float
)

data class PredictActivityRequest(
    val username: String,
    val activity_item: String
)

data class PredictActivityResponse(
    val activity_item: String,
    val points: Float
)
