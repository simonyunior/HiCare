package com.example.hicare.ui.calendar

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hicare.R
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment : Fragment() {

    private val viewModel: CalendarViewModel by viewModels()
    private lateinit var calendarView: CalendarView
    private lateinit var btnAddEvent: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        calendarView = view.findViewById(R.id.calendarView)
        btnAddEvent = view.findViewById(R.id.btnAddEvent)
        recyclerView = view.findViewById(R.id.recyclerView)

        adapter = EventsAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        btnAddEvent.setOnClickListener {
            showAddActivityDialog()
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(GregorianCalendar(year, month, dayOfMonth).time)
            adapter.updateEvents(viewModel.getEvents(selectedDate))
        }

        viewModel.events.observe(viewLifecycleOwner, Observer { events ->
            val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(calendarView.date))
            adapter.updateEvents(events[selectedDate] ?: emptyList())
        })

        return view
    }

    private fun showAddActivityDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_activity, null)
        val etActivity = dialogView.findViewById<EditText>(R.id.etActivity)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSave)

        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        btnSave.setOnClickListener {
            val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(calendarView.date))
            val activity = etActivity.text.toString()
            val hour = timePicker.hour
            val minute = timePicker.minute

            if (activity.isNotEmpty()) {
                val time = String.format(Locale.getDefault(), "%02d:%02d %s", if (hour % 12 == 0) 12 else hour % 12, minute, if (hour >= 12) "PM" else "AM")
                val event = "$time: $activity"

                viewModel.addEvent(selectedDate, event)
                Toast.makeText(context, "Event '$activity' added on $selectedDate at $time", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please enter an activity", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}
