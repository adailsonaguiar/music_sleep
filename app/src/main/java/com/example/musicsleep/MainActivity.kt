package com.example.musicsleep

import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var startBtn: Button
    private lateinit var decreaseBtn: FloatingActionButton
    private lateinit var increaseBtn: FloatingActionButton
    private lateinit var remainingTime: TextView
    private lateinit var audioManager: AudioManager
    private var timer: CountDownTimer? = null
    private var oneMinute: Long = 60000
    private var minuteMultiple = 1
    private var remainingTimeMillis: Long = oneMinute * minuteMultiple

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startBtn = findViewById(R.id.start)
        remainingTime = findViewById(R.id.remainingTime)
        decreaseBtn = findViewById(R.id.decrease)
        increaseBtn= findViewById(R.id.increase)

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        startBtn.setOnClickListener {
            if (timer != null) {
                resetCounter()
                timer?.cancel() // Cancela o temporizador existente se houver
            }
            initializerTimer()
        }

        decreaseBtn.setOnClickListener{
            if(minuteMultiple> 1) {
                minuteMultiple = minuteMultiple - 3
                resetCounter()
            }
        }
        increaseBtn.setOnClickListener{
            minuteMultiple = minuteMultiple + 3
            resetCounter()
        }
    }

    private fun initializerTimer(){
        timer = object : CountDownTimer(remainingTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeMillis = millisUntilFinished
                updateRemainingTime()
            }


            override fun onFinish() {
                if (audioManager.isMusicActive) {
                    audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
                    audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE))
                    audioManager.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PAUSE))
                }
                Toast.makeText(applicationContext, "Música pausada", Toast.LENGTH_SHORT).show()
            }
        }
        timer?.start()
    }

    private fun updateRemainingTime() {
        val min = remainingTimeMillis / 60000
        val sec = (remainingTimeMillis % 60000) / 1000
        val timeText = String.format("%d:%02d", min, sec)
        remainingTime.text = timeText
    }

    private fun resetCounter() {
        timer?.cancel()
        remainingTimeMillis = oneMinute * minuteMultiple
        updateRemainingTime()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}