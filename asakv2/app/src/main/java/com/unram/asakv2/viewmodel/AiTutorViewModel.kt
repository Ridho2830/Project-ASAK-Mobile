package com.unram.asakv2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unram.asakv2.model.ChatMessage

/**
 * AiTutorViewModel — Chat state, kirim pesan ke Gemini API.
 * Mengelola state chat AI Tutor dan komunikasi dengan Gemini API.
 * [RENDI]
 */
class AiTutorViewModel : ViewModel() {

    private val _chatMessages = MutableLiveData<MutableList<ChatMessage>>(mutableListOf())
    val chatMessages: LiveData<MutableList<ChatMessage>> = _chatMessages

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun sendMessage(message: String) {
        _isLoading.value = true
        // TODO: Tambah pesan user ke list
        // TODO: Kirim pesan ke Gemini API via AiTutorRepository
        // TODO: Terima response dan tambah ke list
    }

    fun clearChat() {
        _chatMessages.value = mutableListOf()
    }
}
