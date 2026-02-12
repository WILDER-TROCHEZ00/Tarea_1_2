package com.example.tarea_1_2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tarea_1_2.data.local.AppDbHelper
import com.example.tarea_1_2.data.local.CambioDao
import com.example.tarea_1_2.data.repository.CambioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ConvertState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val lastId: Long? = null
)

class ConverterViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CambioRepository(CambioDao(AppDbHelper(app)))

    private val _state = MutableStateFlow(ConvertState())
    val state: StateFlow<ConvertState> = _state

    fun convert(amount: Double, from: String, to: String) {
        _state.value = ConvertState(isLoading = true)
        viewModelScope.launch {
            try {
                val id = repo.convertAndSave(amount, from, to)
                _state.value = ConvertState(lastId = id)
            } catch (e: Exception) {
                _state.value = ConvertState(error = e.message ?: "Error al convertir")
            }
        }
    }

    fun consumeLastId() {
        _state.value = _state.value.copy(lastId = null)
    }

}
