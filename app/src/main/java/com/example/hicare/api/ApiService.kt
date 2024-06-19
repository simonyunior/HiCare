package com.example.hicare.api

import com.example.hicare.data.LoginRequest
import com.example.hicare.data.LoginResponse
import com.example.hicare.data.PredictActivityRequest
import com.example.hicare.data.PredictActivityResponse
import com.example.hicare.data.PredictRequest
import com.example.hicare.data.PredictResponse
import com.example.hicare.data.RegisterRequest
import com.example.hicare.data.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/register")
    suspend fun register(@Body user: RegisterRequest): Response<RegisterResponse>

    @POST("/login")
    suspend fun login(@Body user: LoginRequest): Response<LoginResponse>

    @POST("/predict")
    suspend fun predictFood(@Body request: PredictRequest): Response<PredictResponse>

    @POST("/predictActivity")
    suspend fun predictActivity(@Body request: PredictActivityRequest): Response<PredictActivityResponse>
}
