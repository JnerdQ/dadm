package com.dadm.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GeminiViewModel : ViewModel() {
    private val apiKey = ""
    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.0-flash",
        apiKey = apiKey
    )

    private val _uiState = MutableStateFlow<GeminiUiState>(GeminiUiState.Initial)
    val uiState: StateFlow<GeminiUiState> = _uiState.asStateFlow()

    fun generateContent(prompt: String) {
        viewModelScope.launch {
            _uiState.value = GeminiUiState.Loading
            try {
                val response = generativeModel.generateContent(prompt)
                _uiState.value = GeminiUiState.Success(response.text ?: "No response")
            } catch (e: Exception) {
                _uiState.value = GeminiUiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}