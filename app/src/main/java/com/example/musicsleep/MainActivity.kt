package com.example.musicsleep

import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.KeyEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var decreaseBtn: Button
    private lateinit var remainingTime: TextView
    private lateinit var audioManager: AudioManager
    private var timer: CountDownTimer? = null
    private var oneMinute: Long = 60000
    private var remainingTimeMillis: Long = oneMinute * 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        decreaseBtn = findViewById(R.id.decrease)
        remainingTime = findViewById(R.id.remainingTime)

        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager

        decreaseBtn.setOnClickListener {
            iniciarTemporizador()
            Toast.makeText(this, "Música pausará em 30 minutos", Toast.LENGTH_SHORT).show()

        }
    }

    private fun iniciarTemporizador() {
        timer?.cancel()

        timer = object : CountDownTimer(remainingTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTimeMillis = millisUntilFinished
                atualizarTextoTempoRestante()
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

    private fun atualizarTextoTempoRestante() {
        val min = remainingTimeMillis / 60000
        val sec = (remainingTimeMillis % 60000) / 1000
        val timeText = String.format("%d:%02d", min, sec)
        remainingTime.text = timeText
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}