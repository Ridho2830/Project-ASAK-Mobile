package com.unram.asakv2.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unram.asakv2.R

/**
 * QuizResultFragment — Hasil quiz: XP, skor, huruf lemah.
 * Menampilkan ringkasan hasil quiz termasuk XP yang didapat dan huruf yang perlu diperbaiki.
 * [RENDI]
 */
class QuizResultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_result, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Tampilkan skor, XP earned, huruf lemah
        // TODO: Setup tombol kembali/ulangi quiz
    }
}
