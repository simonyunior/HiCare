package com.example.hicare.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import com.example.hicare.R
import java.text.DecimalFormat

class DashboardFragment : Fragment() {

    private lateinit var tvTotalCalories: TextView
    private lateinit var tvTotalPoints: TextView
    private lateinit var tvWelcome: TextView
    private lateinit var btnClear: Button
    private val decimalFormat = DecimalFormat("#.##")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        tvTotalCalories = view.findViewById(R.id.tvTotalCalories)
        tvTotalPoints = view.findViewById(R.id.tvTotalPoints)
        tvWelcome = view.findViewById(R.id.tvWelcome)
        btnClear = view.findViewById(R.id.btnClear)

        val username = getUsername()
        tvWelcome.text = "Hello, $username"

        fetchAndDisplayTotals()

        val scaleUp = AnimationUtils.loadAnimation(context, R.anim.scale_up)
        tvTotalCalories.startAnimation(scaleUp)
        tvTotalPoints.startAnimation(scaleUp)

        btnClear.setOnClickListener {
            clearTotals()
        }

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

    private fun clearTotals() {
        val sharedPref = activity?.getSharedPreferences("HiCare", Context.MODE_PRIVATE)
        val editor = sharedPref?.edit()

        editor?.remove("TOTAL_CALORIES")
        editor?.remove("TOTAL_POINTS")

        editor?.apply()

        tvTotalCalories.text = "0 kcal"
        tvTotalPoints.text = "0"
    }
}
