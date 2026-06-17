package com.unram.asakv2.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unram.asakv2.R

/**
 * QuizTipe5Fragment — Drag & drop pasangkan aksara ↔ nama.
 * User mencocokkan aksara dengan nama huruf menggunakan drag & drop.
 * [RENDI]
 */
class QuizTipe5Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_tipe5, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Setup drag & drop UI
        // TODO: Load pasangan aksara ↔ nama
        // TODO: Validasi jawaban pasangan
    }
}
