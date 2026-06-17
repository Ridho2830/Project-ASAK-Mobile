package com.unram.asakv2.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unram.asakv2.R

/**
 * QuizTipe2Fragment — Audio → pilih gambar aksara.
 * Memutar audio pengucapan huruf, user memilih gambar aksara yang sesuai.
 * [RENDI]
 */
class QuizTipe2Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_tipe2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Setup audio player
        // TODO: Load pilihan gambar aksara
        // TODO: Handle pilihan jawaban user
    }
}
