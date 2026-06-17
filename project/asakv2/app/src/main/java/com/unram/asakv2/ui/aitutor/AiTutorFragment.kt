package com.unram.asakv2.ui.aitutor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.unram.asakv2.R
import com.unram.asakv2.viewmodel.AiTutorViewModel

/**
 * AiTutorFragment — Chat bubble UI + chip suggestion.
 * Menampilkan antarmuka chat dengan AI Tutor menggunakan Gemini API.
 * [RENDI]
 */
class AiTutorFragment : Fragment() {

    private lateinit var aiTutorViewModel: AiTutorViewModel
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ai_tutor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        aiTutorViewModel = ViewModelProvider(this)[AiTutorViewModel::class.java]

        // TODO: Setup RecyclerView dengan ChatAdapter
        // TODO: Setup input field dan tombol kirim
        // TODO: Setup chip suggestion
        // TODO: Observe chat messages dari ViewModel
    }
}
