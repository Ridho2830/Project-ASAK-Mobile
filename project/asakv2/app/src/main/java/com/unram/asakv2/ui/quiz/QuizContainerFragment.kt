package com.unram.asakv2.ui.quiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.unram.asakv2.R
import com.unram.asakv2.viewmodel.QuizViewModel

/**
 * QuizContainerFragment — Koordinator tipe quiz + adaptive logic.
 * Mengelola alur quiz dan menampilkan fragment tipe soal yang sesuai.
 * [RENDI]
 */
class QuizContainerFragment : Fragment() {

    private lateinit var quizViewModel: QuizViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quiz_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        quizViewModel = ViewModelProvider(this)[QuizViewModel::class.java]

        // TODO: Observe quiz state dari ViewModel
        // TODO: Load fragment tipe soal yang sesuai berdasarkan adaptive logic
    }
}
