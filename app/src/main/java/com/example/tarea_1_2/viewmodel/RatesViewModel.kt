package com.example.tarea_1_2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tarea_1_2.data.local.AppDbHelper
import com.example.tarea_1_2.data.local.CambioDao
import com.example.tarea_1_2.data.model.Rate
import com.example.tarea_1_2.data.repository.CambioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class RatesState(
    val items: List<Rate> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class RatesViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = CambioRepository(CambioDao(AppDbHelper(app)))

    private val _state = MutableStateFlow(RatesState())
    val state: StateFlow<RatesState> = _state

    fun load() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val list = repo.rates()
                _state.value = RatesState(items = list)
            } catch (e: Exception) {
                _state.value = RatesState(error = e.message ?: "Error cargando tasas")
            }
        }
    }

    fun saveRate(from: String, to: String, rate: Double) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                repo.upsertRate(from, to, rate)
                val list = repo.rates()
                _state.value = RatesState(items = list)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Error guardando tasa")
            }
        }
    }

    fun toggleFavoriteRate(item: com.example.tarea_1_2.data.model.Rate) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                repo.toggleRateFavorite(item.id, item.isFavorite)
                val list = repo.rates() //  ordenado por favoritos
                _state.value = RatesState(items = list)
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message ?: "Error actualizando favorito")
            }
        }
    }

}
