package com.nilhcem.officenoisedetector

import android.arch.lifecycle.ViewModel
import android.util.Log

class MainViewModel : ViewModel() {

    companion object {
        private val TAG = MainViewModel::class.java.simpleName!!
    }

    val decibelsLiveData by lazy { DecibelsLiveData() }

    override fun onCleared() {
        Log.i(TAG, "onCleared")
    }
}
