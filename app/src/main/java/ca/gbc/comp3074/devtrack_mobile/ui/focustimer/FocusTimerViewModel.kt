package ca.gbc.comp3074.devtrack_mobile.ui.focustimer

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FocusTimerViewModel : ViewModel() {

    private var countdownTimer: CountDownTimer? = null

    private val _timeLeftFormatted = MutableLiveData("25:00")
    val timeLeftFormatted: LiveData<String> = _timeLeftFormatted

    private val _isTimerRunning = MutableLiveData(false)
    val isTimerRunning: LiveData<Boolean> = _isTimerRunning

    private val totalTimeInMillis: Long = 25 * 60 * 1000 // 25 Minutes Pomodoro

    fun startTimer() {
        if (_isTimerRunning.value == true) return

        countdownTimer = object : CountDownTimer(totalTimeInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = (millisUntilFinished / 1000) / 60
                val seconds = (millisUntilFinished / 1000) % 60
                _timeLeftFormatted.value = String.format("%02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                _timeLeftFormatted.value = "00:00"
                _isTimerRunning.value = false
            }
        }.start()

        _isTimerRunning.value = true
    }

    fun pauseTimer() {
        countdownTimer?.cancel()
        _isTimerRunning.value = false
    }

    fun resetTimer() {
        countdownTimer?.cancel()
        _isTimerRunning.value = false
        _timeLeftFormatted.value = "25:00"
    }

    override fun onCleared() {
        super.onCleared()
        countdownTimer?.cancel()
    }
}