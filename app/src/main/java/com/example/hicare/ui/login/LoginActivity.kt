package com.example.hicare.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hicare.MainActivity
import com.example.hicare.R
import com.example.hicare.api.ApiService
import com.example.hicare.api.RetrofitClient
import com.example.hicare.data.LoginRequest
import kotlinx.coroutines.launch
import java.util.UUID

class LoginActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        apiService = RetrofitClient.instance.create(ApiService::class.java)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener {
            val username = findViewById<EditText>(R.id.etUser).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()

            lifecycleScope.launch {
                val response = apiService.login(LoginRequest(username, password))
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.status == "success") {
                        val token = UUID.randomUUID().toString()
                        saveUserInfo(username, token)
                        Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()
                        navigateToDashboard()
                    } else {
                        Toast.makeText(this@LoginActivity, loginResponse?.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)
        tvCreateAccount.setOnClickListener {
            val intent = Intent(this, CreateAccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun saveUserInfo(username: String, token: String) {
        val sharedPref = getSharedPreferences("HiCare", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("USERNAME", username)
            putString("TOKEN", token)
            apply()
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
