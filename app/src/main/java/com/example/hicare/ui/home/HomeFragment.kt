package com.example.hicare.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.hicare.R
import com.example.hicare.api.ApiService
import com.example.hicare.api.RetrofitClient
import com.example.hicare.data.PredictRequest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var apiService: ApiService
    private lateinit var etMealInput: EditText
    private lateinit var btnPredictCalories: Button
    private lateinit var tvResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_food, container, false)

        apiService = RetrofitClient.instance.create(ApiService::class.java)

        etMealInput = view.findViewById(R.id.etMealInput)
        btnPredictCalories = view.findViewById(R.id.btnPredictCalories)
        tvResult = view.findViewById(R.id.tvResult)

        btnPredictCalories.setOnClickListener {
            val mealInput = etMealInput.text.toString()
            val capitalizedMealInput = capitalizeWords(mealInput)
            val username = getUsername()

            if (capitalizedMealInput.isNotEmpty() && username.isNotEmpty()) {
                predictCalories(username, capitalizedMealInput)
            } else {
                Toast.makeText(context, "Please enter a food item", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun capitalizeWords(input: String): String {
        return input.split(" ").joinToString(" ") { it.capitalize() }
    }

    private fun getUsername(): String {
        val sharedPref = activity?.getSharedPreferences("HiCare", Context.MODE_PRIVATE)
        return sharedPref?.getString("USERNAME", "") ?: ""
    }

    private fun predictCalories(username: String, mealInput: String) {
        lifecycleScope.launch {
            val predictFoodResponse = apiService.predictFood(PredictRequest(username, mealInput))
            if (predictFoodResponse.isSuccessful) {
                val foodResponse = predictFoodResponse.body()
                if (foodResponse?.calories != null) {
                    tvResult.text = "Calories: ${foodResponse.calories}"
                    tvResult.visibility = View.VISIBLE
                    tvResult.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
                    saveCalories(foodResponse.calories)
                } else {
                    tvResult.text = "Food or drink not found"
                }
            } else {
                tvResult.text = "Failed to predict calories"
            }
        }
    }

    private fun saveCalories(calories: Float) {
        val sharedPref = activity?.getSharedPreferences("HiCare", Context.MODE_PRIVATE)
        val totalCalories = sharedPref?.getFloat("TOTAL_CALORIES", 0f) ?: 0f
        with(sharedPref?.edit()) {
            this?.putFloat("TOTAL_CALORIES", totalCalories + calories)
            this?.apply()
        }
    }
}
