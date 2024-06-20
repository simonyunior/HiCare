package com.example.hicare.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CalendarViewModel : ViewModel() {
    private val _events = MutableLiveData<MutableMap<String, MutableList<String>>>()
    val events: LiveData<MutableMap<String, MutableList<String>>> get() = _events

    init {
        _events.value = mutableMapOf()
    }

    fun addEvent(date: String, event: String) {
        val currentEvents = _events.value ?: mutableMapOf()
        if (!currentEvents.containsKey(date)) {
            currentEvents[date] = mutableListOf()
        }
        currentEvents[date]?.add(event)
        _events.value = currentEvents
    }

    fun getEvents(date: String): List<String> {
        return _events.value?.get(date) ?: emptyList()
    }

    fun setEvents(events: MutableMap<String, MutableList<String>>) {
        _events.value = events
    }
}
