
package ca.gbc.comp3074.devtrack_mobile

import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class TimerActivity : AppCompatActivity() {

    private lateinit var textTimerCountdown: TextView
    private lateinit var spinnerDuration: Spinner
    private lateinit var editCustomDuration: EditText
    private lateinit var btnApplyDuration: Button
    private lateinit var btnStartTimer: Button
    private lateinit var btnPauseTimer: Button

    private var timer: CountDownTimer? = null

    private var selectedDuration = 25 * 60 * 1000L
    private var timeLeft = selectedDuration
    private var endTime = 0L
    private var isRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        textTimerCountdown = findViewById(R.id.textTimerCountdown)
        spinnerDuration = findViewById(R.id.spinnerDuration)
        editCustomDuration = findViewById(R.id.editCustomDuration)
        btnApplyDuration = findViewById(R.id.btnApplyDuration)
        btnStartTimer = findViewById(R.id.btnStartTimer)
        btnPauseTimer = findViewById(R.id.btnPauseTimer)

        val btnResetTimer =
            findViewById<Button>(R.id.btnResetTimer)

        val btnBack =
            findViewById<Button>(R.id.btnBackToDashboard)

        FocusNotificationHelper.createNotificationChannel(this)

        setupDurationSpinner()

        btnApplyDuration.setOnClickListener {
            applyDuration()
        }

        btnStartTimer.setOnClickListener {
            startTimer()
        }

        btnPauseTimer.setOnClickListener {
            pauseTimer()
        }

        btnResetTimer.setOnClickListener {
            resetTimer()
        }

        btnBack.setOnClickListener {
            finish()
        }

        if (savedInstanceState != null) {

            selectedDuration = savedInstanceState.getLong(
                "selectedDuration",
                selectedDuration
            )

            timeLeft = savedInstanceState.getLong(
                "timeLeft",
                selectedDuration
            )

            endTime = savedInstanceState.getLong("endTime")

            val wasRunning =
                savedInstanceState.getBoolean("isRunning")

            if (wasRunning) {

                timeLeft = (
                        endTime - SystemClock.elapsedRealtime()
                        ).coerceAtLeast(0L)

                if (timeLeft > 0L) {
                    startTimer()
                } else {
                    completeSession()
                }
            }
        }

        updateCountdown()
        updateButtons()
    }

    private fun setupDurationSpinner() {

        val durations = arrayOf(
            "15 minutes",
            "25 minutes",
            "45 minutes",
            "60 minutes",
            "Custom"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            durations
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerDuration.adapter = adapter
        spinnerDuration.setSelection(1)

        spinnerDuration.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    editCustomDuration.visibility =
                        if (position == 4) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>?
                ) {}
            }
    }

    private fun applyDuration() {

        val minutes = when (spinnerDuration.selectedItemPosition) {

            0 -> 15
            1 -> 25
            2 -> 45
            3 -> 60

            else -> {
                editCustomDuration.text.toString()
                    .toIntOrNull() ?: 0
            }
        }

        if (minutes !in 1..180) {

            editCustomDuration.error =
                "Enter a duration between 1 and 180 minutes"

            return
        }

        selectedDuration = minutes * 60 * 1000L

        resetTimer()

        Toast.makeText(
            this,
            "Timer set to $minutes minutes",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun startTimer() {

        if (isRunning || timeLeft <= 0L) return

        isRunning = true

        endTime = SystemClock.elapsedRealtime() + timeLeft

        timer = object : CountDownTimer(timeLeft, 1000) {

            override fun onTick(millisUntilFinished: Long) {

                timeLeft = millisUntilFinished
                updateCountdown()
            }

            override fun onFinish() {
                completeSession()
            }

        }.start()

        updateButtons()
    }

    private fun pauseTimer() {

        if (!isRunning) return

        timeLeft = (
                endTime - SystemClock.elapsedRealtime()
                ).coerceAtLeast(0L)

        timer?.cancel()
        timer = null
        isRunning = false

        updateCountdown()
        updateButtons()
    }

    private fun resetTimer() {

        timer?.cancel()
        timer = null

        isRunning = false
        timeLeft = selectedDuration

        updateCountdown()
        updateButtons()
    }

    private fun completeSession() {

        timer = null
        timeLeft = 0L
        isRunning = false

        updateCountdown()
        updateButtons()

        FocusNotificationHelper.showCompletionNotification(this)

        Toast.makeText(
            this,
            "Focus session completed!",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun updateCountdown() {

        val totalSeconds = (timeLeft + 999) / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        textTimerCountdown.text = String.format(
            Locale.getDefault(),
            "%02d:%02d",
            minutes,
            seconds
        )
    }

    private fun updateButtons() {

        btnStartTimer.isEnabled = !isRunning && timeLeft > 0L
        btnPauseTimer.isEnabled = isRunning

        spinnerDuration.isEnabled = !isRunning
        editCustomDuration.isEnabled = !isRunning
        btnApplyDuration.isEnabled = !isRunning

        btnStartTimer.text =
            if (timeLeft < selectedDuration && timeLeft > 0L) {
                "RESUME FOCUS"
            } else {
                "START FOCUS"
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putLong("selectedDuration", selectedDuration)
        outState.putLong("timeLeft", timeLeft)
        outState.putLong("endTime", endTime)
        outState.putBoolean("isRunning", isRunning)

        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {

        timer?.cancel()

        super.onDestroy()
    }
}
