package com.example.moneyhub.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _currentDestination = MutableLiveData<Int>()
    val currentDestination: LiveData<Int> get() = _currentDestination

    fun updateCurrentDestination(destinatoinId: Int) {
        _currentDestination.value = destinatoinId
    }
}