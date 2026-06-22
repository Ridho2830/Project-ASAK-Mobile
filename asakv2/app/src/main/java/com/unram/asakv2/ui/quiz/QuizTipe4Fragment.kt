package com.unram.asakv2.ui.quiz

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.unram.asakv2.R
import com.unram.asakv2.ai.VoiceRecognizer
import com.unram.asakv2.databinding.FragmentQuizTipe4Binding
import com.unram.asakv2.utils.Constants
import com.unram.asakv2.utils.XpCalculator
import kotlinx.coroutines.launch
import java.io.File

class QuizTipe4Fragment : Fragment() {

    private var _binding: FragmentQuizTipe4Binding? = null
    private val binding get() = _binding!!

    private lateinit var hurufId: String
    private var expFull = 20
    private var isStreakMode = true
    private var attemptCount = 0
    private var isRecording = false
    private var audioFile: File? = null
    private lateinit var voiceRecognizer: VoiceRecognizer
    private var countdownTimer: CountDownTimer? = null

    companion object {
        private const val RECORD_PERMISSION_CODE = 101

        fun newInstance(hurufId: String, expFull: Int, isStreakMode: Boolean): QuizTipe4Fragment {
            return QuizTipe4Fragment().apply {
                arguments = Bundle().apply {
                    putString("hurufId", hurufId)
                    putInt("expFull", expFull)
                    putBoolean("isStreakMode", isStreakMode)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentQuizTipe4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hurufId = arguments?.getString("hurufId") ?: "ha"
        expFull = arguments?.getInt("expFull") ?: 20
        isStreakMode = arguments?.getBoolean("isStreakMode") ?: true

        voiceRecognizer = VoiceRecognizer(requireContext())

        setupUI()
        loadAksaraImage()
    }

    private fun loadAksaraImage() {
        try {
            val stream = requireContext().assets.open("aksara/gambar/$hurufId.png")
            val drawable = android.graphics.drawable.Drawable.createFromStream(stream, null)
            binding.ivAksara.setImageDrawable(drawable)
            binding.tvHurufLabel.text = Constants.HURUF_DISPLAY[hurufId] ?: hurufId.uppercase()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupUI() {
        binding.btnNext.isEnabled = false
        binding.btnNext.alpha = 0.5f

        if (!isStreakMode) {
            binding.btnNext.visibility = View.GONE
        }

        binding.btnMic.setOnClickListener {
            if (!isRecording && (isStreakMode.not() || attemptCount < 2)) {
                checkPermissionAndRecord()
            }
        }
    }

    private fun checkPermissionAndRecord() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_PERMISSION_CODE
            )
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        isRecording = true
        binding.btnMic.isEnabled = false
        binding.btnMic.alpha = 0.5f
        binding.tvStatus.text = "Merekam... 3 detik"

        audioFile = voiceRecognizer.startRecording()

        // Auto stop after 3 seconds
        var remaining = 3
        countdownTimer = object : CountDownTimer(Constants.VOICE_RECORD_DURATION_MS, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvStatus.text = "Merekam... $remaining detik"
                remaining--
            }
            override fun onFinish() {
                stopAndSubmit()
            }
        }.start()
    }

    private fun stopAndSubmit() {
        voiceRecognizer.stopRecording()
        isRecording = false
        binding.tvStatus.text = "Mengirim ke server..."

        val file = audioFile ?: return

        lifecycleScope.launch {
            val score = voiceRecognizer.recognizeTarget(file, hurufId)
            handleVoiceScore(score)
        }
    }

    private fun handleVoiceScore(score: Float) {
        val isAccurate = voiceRecognizer.isAccurate(score)
        attemptCount++

        if (!isStreakMode) {
            // Mode bebas: tampilkan akurasi, ndk ada EXP, mic tetap bisa dipencet lagi
            binding.tvStatus.text = if (isAccurate)
                "Akurasi suara: ${score.toInt()}% — Tepat!"
            else
                "Akurasi suara: ${score.toInt()}% — Coba lagi"
            binding.btnMic.isEnabled = true
            binding.btnMic.alpha = 1.0f
            return
        }

        when {
            isAccurate -> {
                val expEarned = XpCalculator.calculateRetryExp(expFull, isFirstAttempt = attemptCount == 1)
                binding.btnMic.isEnabled = false
                showVoiceResult(true, score, expEarned)
            }
            attemptCount < 2 -> {
                binding.tvStatus.text = "Akurasi: ${score.toInt()}% — Coba sekali lagi!"
                binding.btnMic.isEnabled = true
                binding.btnMic.alpha = 1.0f
            }
            else -> {
                binding.btnMic.isEnabled = false
                showVoiceResult(false, score, 0)
            }
        }
    }

    private fun showVoiceResult(isCorrect: Boolean, score: Float, expEarned: Int) {
        val nextStreak = (parentFragment as? QuizContainerFragment)?.getNextStreakIfCorrect() ?: 0
        val expPart = if (isCorrect) XpCalculator.buildFeedbackMessage(expEarned, nextStreak) else "0 EXP"

        QuizFeedbackDialog.show(
            context = requireContext(),
            isSuccess = isCorrect,
            message = "Akurasi: ${score.toInt()}%\n$expPart"
        ) {
            binding.btnNext.isEnabled = true
            binding.btnNext.alpha = 1.0f
            notifyParent(expEarned, isCorrect)
        }
    }

    private fun notifyParent(expEarned: Int, isCorrect: Boolean) {
        (parentFragment as? QuizContainerFragment)?.onQuizFragmentResult(expEarned, isCorrect)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countdownTimer?.cancel()
        voiceRecognizer.cleanup()
        _binding = null
    }
}