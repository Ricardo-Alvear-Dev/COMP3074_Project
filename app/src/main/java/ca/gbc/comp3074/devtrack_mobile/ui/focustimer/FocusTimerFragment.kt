package ca.gbc.comp3074.devtrack_mobile.ui.focustimer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ca.gbc.comp3074.devtrack_mobile.databinding.FragmentFocusTimerBinding

class FocusTimerFragment : Fragment() {

    private var _binding: FragmentFocusTimerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FocusTimerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFocusTimerBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[FocusTimerViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.timeLeftFormatted.observe(viewLifecycleOwner) { timeStr ->
            binding.textTimerCountdown.text = timeStr
        }

        viewModel.isTimerRunning.observe(viewLifecycleOwner) { isRunning ->
            binding.btnStartTimer.isEnabled = !isRunning
            binding.btnPauseTimer.isEnabled = isRunning
        }

        binding.btnStartTimer.setOnClickListener {
            viewModel.startTimer()
        }

        binding.btnPauseTimer.setOnClickListener {
            viewModel.pauseTimer()
        }

        binding.btnResetTimer.setOnClickListener {
            viewModel.resetTimer()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}