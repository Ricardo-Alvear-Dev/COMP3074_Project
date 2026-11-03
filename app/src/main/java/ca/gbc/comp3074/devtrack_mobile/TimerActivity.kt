package ca.gbc.comp3074.devtrack_mobile

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TimerActivity : AppCompatActivity() {

    private lateinit var textTimerCountdown: TextView

    private var timer: CountDownTimer? = null
    private var timeLeft = 25 * 60 * 1000L
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        textTimerCountdown = findViewById(R.id.textTimerCountdown)

        val btnStartTimer = findViewById<Button>(R.id.btnStartTimer)
        val btnPauseTimer = findViewById<Button>(R.id.btnPauseTimer)
        val btnResetTimer = findViewById<Button>(R.id.btnResetTimer)
        val btnBack = findViewById<Button>(R.id.btnBackToDashboard)

        updateCountdown()

        btnStartTimer.setOnClickListener { startTimer() }
        btnPauseTimer.setOnClickListener { pauseTimer() }
        btnResetTimer.setOnClickListener { resetTimer() }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun startTimer() {
        if (isRunning || timeLeft <= 0L) return

        isRunning = true

        timer = object : CountDownTimer(timeLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft = millisUntilFinished
                updateCountdown()
            }

            override fun onFinish() {
                timeLeft = 0L
                isRunning = false
                updateCountdown()
            }
        }.start()
    }

    private fun pauseTimer() {
        timer?.cancel()
        timer = null
        isRunning = false
    }

    private fun resetTimer() {
        timer?.cancel()
        timer = null
        isRunning = false
        timeLeft = 25 * 60 * 1000L
        updateCountdown()
    }

    private fun updateCountdown() {
        val minutes = timeLeft / 1000 / 60
        val seconds = (timeLeft / 1000) % 60

        textTimerCountdown.text = String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        timer?.cancel()
        super.onDestroy()
    }
}