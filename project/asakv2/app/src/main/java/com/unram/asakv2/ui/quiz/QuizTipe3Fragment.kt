package com.unram.asakv2.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unram.asakv2.R

/**
 * QuizTipe3Fragment — Lihat aksara → tulis di canvas (CNN).
 * Menampilkan aksara target dan canvas untuk menulis tangan, dikenali oleh CNN.
 * [RENDI]
 */
class QuizTipe3Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_tipe3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Tampilkan aksara target
        // TODO: Setup CanvasView untuk menulis tangan
        // TODO: Kirim gambar canvas ke HandwritingRecognizer (CNN via Hugging Face)
    }
}
