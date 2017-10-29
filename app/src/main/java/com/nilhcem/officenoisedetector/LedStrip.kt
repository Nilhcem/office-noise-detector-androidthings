package com.nilhcem.officenoisedetector

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.graphics.Color
import com.google.android.things.contrib.driver.apa102.Apa102

class LedStrip : LifecycleObserver {

    companion object {
        private const val SPI_NAME = "SPI0.0"
        private const val NUM_LEDS = 60
        private const val LED_BRIGHTNESS = 6 // 0 ... 31

        private const val HSV_GREEN = 90
        private const val HSV_RED = 0

        private const val MIN_DECIBELS = 30
        private const val MAX_DECIBELS = 90

        private val LED_MODE = Apa102.Mode.BGR
    }

    private var ledStrip: Apa102? = null
    private val colors = IntArray(NUM_LEDS)

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun start() {
        ledStrip = Apa102(SPI_NAME, LED_MODE).apply {
            brightness = LED_BRIGHTNESS
            write(IntArray(NUM_LEDS))
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stop() {
        ledStrip?.close().also { ledStrip = null }
    }

    fun showDecibels(decibels: Float) {
        var decibelLevel: Float

        for (i in 0 until NUM_LEDS) {
            decibelLevel = i.toFloat() * (MAX_DECIBELS - MIN_DECIBELS) / (NUM_LEDS - 1) + MIN_DECIBELS

            colors[i] = if (decibels > decibelLevel) {
                Color.HSVToColor(255, floatArrayOf((HSV_GREEN - (i.toFloat() * (HSV_GREEN - HSV_RED) / (NUM_LEDS - 1)) + 360) % 360, 1.0f, 1.0f))
            } else {
                0
            }
        }
        ledStrip!!.write(colors)
    }
}
