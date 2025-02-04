package com.dadm.webservices


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log

class EquiposViewModel : ViewModel() {
    private val _equipos = MutableStateFlow<List<Equipo>>(emptyList())
    val equipos: StateFlow<List<Equipo>> = _equipos.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val apiService: EquiposApiService = Retrofit.Builder()
        .baseUrl("https://www.datos.gov.co/resource/")

        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(EquiposApiService::class.java)

    init {
        loadEquipos()
    }

    private fun loadEquipos() {
        viewModelScope.launch {
            try {
                val result = apiService.getEquipos()
                _equipos.value = result
                // Print the data to Logcat
                Log.d("EquiposViewModel", "Data from API: $result")
            } catch (e: Exception) {
                // Log the error as well
                Log.e("EquiposViewModel", "Error loading equipos", e)
            }
        }
    }


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
