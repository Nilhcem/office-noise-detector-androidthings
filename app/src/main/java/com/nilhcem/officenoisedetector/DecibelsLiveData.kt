package com.nilhcem.officenoisedetector

import android.arch.lifecycle.LiveData
import android.media.MediaRecorder
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import java.io.IOException
import java.lang.RuntimeException

class DecibelsLiveData(private val frequencyMs: Long = 250) : LiveData<Float>() {

    companion object {
        private val TAG = DecibelsLiveData::class.java.simpleName!!
        private const val HANDLER_MSG_GET_DECIBELS = 1
    }

    private val mediaRecorder = MediaRecorder()
    private var handler: Handler? = null
    private var handlerThread: HandlerThread? = null

    private val handlerCallback = { msg: Message ->
        if (msg.what == HANDLER_MSG_GET_DECIBELS) {
            // Get the sound pressure value
            val volume = mediaRecorder.maxAmplitude
            if (volume != 0) {
                // Change the sound pressure value to the decibel value
                val decibels = 20 * Math.log10(volume.toDouble()).toFloat()
                postValue(decibels)
            }

            handler?.sendEmptyMessageDelayed(HANDLER_MSG_GET_DECIBELS, frequencyMs)
        }
        true
    }

    override fun onActive() {
        Log.i(TAG, "onActive")
        startRecording()
    }

    override fun onInactive() {
        Log.i(TAG, "onInactive")
        stopRecording()
    }

    fun startRecording() {
        with(mediaRecorder) {
            try {
                reset()
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile("/dev/null")

                prepare()
                start()

                // Start handler thread
                handlerThread = HandlerThread(TAG).also { handlerThread ->
                    handlerThread.start()
                    handler = Handler(handlerThread.looper, handlerCallback).also { handler ->
                        handler.sendEmptyMessage(HANDLER_MSG_GET_DECIBELS)
                    }
                }
            } catch (e: IllegalStateException) {
                Log.e(TAG, "Error initializing mediaRecorder", e)
            } catch (e: IOException) {
                Log.e(TAG, "Error initializing mediaRecorder", e)
            } catch (e: RuntimeException) {
                Log.e(TAG, "Error initializing mediaRecorder. Is permission allowed?", e)
            }
        }
    }

    fun stopRecording() {
        handler?.removeMessages(HANDLER_MSG_GET_DECIBELS).also { handler = null }
        handlerThread?.quitSafely().also { handlerThread = null }

        try {
            mediaRecorder.stop()
            mediaRecorder.release()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Error stopping mediaRecorder")
        }
    }
}
