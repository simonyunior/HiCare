package com.example.hicare.ui.login

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.hicare.R
import com.example.hicare.api.ApiService
import com.example.hicare.api.RetrofitClient
import com.example.hicare.data.RegisterRequest
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import java.util.regex.Pattern

class CreateAccountActivity : AppCompatActivity() {

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        apiService = RetrofitClient.instance.create(ApiService::class.java)

        val etNewUsername = findViewById<EditText>(R.id.etNewUsername)
        val etNewEmail = findViewById<EditText>(R.id.etNewEmail)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val btnCreateAccount = findViewById<Button>(R.id.btnCreateAccount)

        btnCreateAccount.setOnClickListener {
            val username = etNewUsername.text.toString()
            val email = etNewEmail.text.toString()
            val password = etNewPassword.text.toString()

            if (validateInput(username, email, password)) {
                val registerRequest = RegisterRequest(username, email, password)
                registerUser(registerRequest)
            }
        }
    }

    private fun validateInput(username: String, email: String, password: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        val passwordPattern = Pattern.compile(
            "^" +
                    "(?=.*[0-9])" +       //at least 1 digit
                    "(?=.*[a-z])" +       //at least 1 lower case letter
                    "(?=.*[A-Z])" +       //at least 1 upper case letter
                    ".{6,}" +             //at least 6 characters
                    "$"
        )

        return when {
            username.isEmpty() -> {
                showSnackbar("Username cannot be empty")
                false
            }
            !email.matches(emailPattern.toRegex()) -> {
                showSnackbar("Invalid email address")
                false
            }
            !passwordPattern.matcher(password).matches() -> {
                showSnackbar("Password must contain at least 6 characters, including uppercase, lowercase, and numbers")
                false
            }
            else -> true
        }
    }

    private fun registerUser(registerRequest: RegisterRequest) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.register(registerRequest)
                runOnUiThread {
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        if (registerResponse?.status == "success") {
                            showSnackbar(registerResponse.message)
                            runOnUiThread {
                                finish()  // Close the activity
                            }
                        } else {
                            showSnackbar(registerResponse?.message ?: "Unknown error")
                        }
                    } else {
                        val errorMessage = parseError(response.errorBody())
                        showSnackbar("Registration failed: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showSnackbar("Registration failed: ${e.message}")
                }
            }
        }
    }

    private fun parseError(response: ResponseBody?): String {
        return try {
            response?.string()?.let {
                val jsonObject = JSONObject(it)
                jsonObject.getString("message")
            } ?: "Unknown error"
        } catch (e: Exception) {
            "Unknown error"
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(R.id.coordinatorLayout), message, Snackbar.LENGTH_LONG).show()
    }
}
