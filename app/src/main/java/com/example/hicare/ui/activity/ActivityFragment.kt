package com.example.hicare.ui.activity

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
import com.example.hicare.data.PredictActivityRequest
import kotlinx.coroutines.launch

class ActivityFragment : Fragment() {

    private lateinit var apiService: ApiService
    private lateinit var etActivityInput: EditText
    private lateinit var btnPredictPoints: Button
    private lateinit var tvActivityResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_activity, container, false)

        apiService = RetrofitClient.instance.create(ApiService::class.java)

        etActivityInput = view.findViewById(R.id.etActivityInput)
        btnPredictPoints = view.findViewById(R.id.btnPredictPoints)
        tvActivityResult = view.findViewById(R.id.tvActivityResult)

        btnPredictPoints.setOnClickListener {
            val activityInput = etActivityInput.text.toString()
            val username = getUsername()

            if (activityInput.isNotEmpty() && username.isNotEmpty()) {
                predictPoints(username, activityInput)
            } else {
                Toast.makeText(context, "Please enter an activity", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun getUsername(): String {
        val sharedPref = activity?.getSharedPreferences("HiCare", Context.MODE_PRIVATE)
        return sharedPref?.getString("USERNAME", "") ?: ""
    }

    private fun predictPoints(username: String, activityInput: String) {
        lifecycleScope.launch {
            val predictActivityResponse = apiService.predictActivity(PredictActivityRequest(username, activityInput))
            if (predictActivityResponse.isSuccessful) {
                val activityResponse = predictActivityResponse.body()
                if (activityResponse?.points != null) {
                    tvActivityResult.text = "Points: ${activityResponse.points}"
                    tvActivityResult.visibility = View.VISIBLE
                    tvActivityResult.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fade_in))
                    savePoints(activityResponse.points)
                } else {
                    tvActivityResult.text = "Activity not found"
                }
            } else {
                tvActivityResult.text = "Failed to predict points"
            }
        }
    }

    private fun savePoints(points: Float) {
        val sharedPref = activity?.getSharedPreferences("HiCare", Context.MODE_PRIVATE)
        val totalPoints = sharedPref?.getFloat("TOTAL_POINTS", 0f) ?: 0f
        with(sharedPref?.edit()) {
            this?.putFloat("TOTAL_POINTS", totalPoints + points)
            this?.apply()
        }
    }
}
