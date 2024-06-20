package com.example.hicare.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import com.example.hicare.R
import android.widget.TextView
import java.text.DecimalFormat

class DashboardFragment : Fragment() {

    private lateinit var tvTotalCalories: TextView
    private lateinit var tvTotalPoints: TextView
    private lateinit var tvWelcome: TextView
    private val decimalFormat = DecimalFormat("#.##")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        tvTotalCalories = view.findViewById(R.id.tvTotalCalories)
        tvTotalPoints = view.findViewById(R.id.tvTotalPoints)
        tvWelcome = view.findViewById(R.id.tvWelcome)

        val username = getUsername()
        tvWelcome.text = "Hello, $username"

        fetchAndDisplayTotals()

        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)
        tvTotalCalories.startAnimation(scaleUp)
        tvTotalPoints.startAnimation(scaleUp)

        return view
    }

    private fun getUsername(): String {
        val sharedPref = activity?.getSharedPreferences("HiCare", Context.MODE_PRIVATE)
        return sharedPref?.getString("USERNAME", "") ?: ""
    }

    private fun fetchAndDisplayTotals() {
        val sharedPref = activity?.getSharedPreferences("HiCare", Context.MODE_PRIVATE)
        val totalCalories = sharedPref?.getFloat("TOTAL_CALORIES", 0f) ?: 0f
        val totalPoints = sharedPref?.getFloat("TOTAL_POINTS", 0f)?.toInt() ?: 0

        tvTotalCalories.text = "${decimalFormat.format(totalCalories)} kcal"
        tvTotalPoints.text = "$totalPoints"
    }
}
