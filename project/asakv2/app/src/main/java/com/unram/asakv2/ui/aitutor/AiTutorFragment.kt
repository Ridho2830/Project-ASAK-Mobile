package com.unram.asakv2.ui.aitutor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.unram.asakv2.R
import com.unram.asakv2.viewmodel.AiTutorViewModel

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

    }
}
