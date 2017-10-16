package com.nilhcem.officenoisedetector

import android.app.Activity
import android.os.Bundle
import android.util.Log

class MainActivity : Activity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
    }
}
