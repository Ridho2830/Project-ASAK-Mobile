package com.unram.asakv2.ui.quiz

import android.media.MediaPlayer
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.unram.asakv2.databinding.FragmentQuizTipe0Binding
import com.unram.asakv2.utils.Constants

class QuizTipe0Fragment : Fragment() {

    private var _binding: FragmentQuizTipe0Binding? = null
    private val binding get() = _binding!!

    private lateinit var hurufId: String
    private var expFull = 5
    private var isStreakMode = true

    private var mediaPlayer: MediaPlayer? = null
    private var hasPlayedSound = false

    companion object {
        fun newInstance(hurufId: String, expFull: Int, isStreakMode: Boolean): QuizTipe0Fragment {
            return QuizTipe0Fragment().apply {
                arguments = Bundle().apply {
                    putString("hurufId", hurufId)
                    putInt("expFull", expFull)
                    putBoolean("isStreakMode", isStreakMode)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizTipe0Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        hurufId = arguments?.getString("hurufId") ?: "ha"
        expFull = arguments?.getInt("expFull") ?: 5
        isStreakMode = arguments?.getBoolean("isStreakMode") ?: true

        setupDisplay()
    }

    private fun setupDisplay() {
        // Tampilkan nama huruf dalam alfabet (misal: "Ha", "Na", dll)
        val displayName = Constants.HURUF_DISPLAY[hurufId] ?: hurufId.replaceFirstChar { it.uppercase() }
        binding.tvHurufLatin.text = displayName

        // Tampilkan gambar aksara dari assets
        try {
            val inputStream = requireContext().assets.open("aksara/gambar/$hurufId.png")
            val drawable = android.graphics.drawable.Drawable.createFromStream(inputStream, null)
            binding.ivAksara.setImageDrawable(drawable)
        } catch (e: Exception) {
            binding.ivAksara.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        // Tombol Next awalnya disabled — harus tekan suara dulu
        binding.btnNext.isEnabled = false
        binding.btnNext.alpha = 0.5f

        // Tombol putar suara
        binding.btnPlaySound.setOnClickListener {
            playSound()
        }

        binding.btnNext.setOnClickListener {
            // Lanjut ke soal berikutnya — kirim hasil ke parent
            notifyParent(expFull, true)
        }
    }

    private fun playSound() {
        // Release player lama kalau ada
        mediaPlayer?.release()
        mediaPlayer = null

        try {
            val afd = requireContext().assets.openFd("aksara/audio/$hurufId.mp3")
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                prepare()
                start()
                setOnCompletionListener {
                    // Aktifkan tombol Next setelah suara selesai (minimal 1x)
                    if (!hasPlayedSound) {
                        hasPlayedSound = true
                        binding.btnNext.isEnabled = true
                        binding.btnNext.alpha = 1.0f
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Audio tidak ditemukan", Toast.LENGTH_SHORT).show()
            // Tetap aktifkan next kalau audio error agar tidak stuck
            if (!hasPlayedSound) {
                hasPlayedSound = true
                binding.btnNext.isEnabled = true
                binding.btnNext.alpha = 1.0f
            }
        }
    }

    private fun notifyParent(expEarned: Int, isCorrect: Boolean) {
        (parentFragment as? QuizContainerFragment)?.onQuizFragmentResult(expEarned, isCorrect)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
}