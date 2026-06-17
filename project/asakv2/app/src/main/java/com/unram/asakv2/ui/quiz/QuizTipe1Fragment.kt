package com.unram.asakv2.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unram.asakv2.R

/**
 * QuizTipe1Fragment — Gambar → pilih nama (4 opsi).
 * Menampilkan gambar aksara dan 4 pilihan jawaban berupa nama huruf.
 * [RENDI]
 */
class QuizTipe1Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_tipe1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Load gambar aksara dan 4 opsi jawaban
        // TODO: Handle pilihan jawaban user
    }
}
