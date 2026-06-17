package com.unram.asakv2.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unram.asakv2.R

/**
 * QuizTipe4Fragment — Lihat aksara → ucapkan (Voice).
 * Menampilkan aksara target, user mengucapkan nama huruf menggunakan voice recognition.
 * [RENDI]
 */
class QuizTipe4Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_tipe4, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Tampilkan aksara target
        // TODO: Setup VoiceRecognizer untuk menangkap ucapan user
        // TODO: Validasi jawaban suara
    }
}
