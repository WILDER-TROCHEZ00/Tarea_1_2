package com.example.tarea_1_2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tarea_1_2.data.local.AppDbHelper
import com.example.tarea_1_2.data.local.CambioDao
import com.example.tarea_1_2.data.model.Conversion
import com.example.tarea_1_2.data.repository.CambioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CambioRepository(CambioDao(AppDbHelper(app)))

    private val _items = MutableStateFlow<List<Conversion>>(emptyList())
    val items: StateFlow<List<Conversion>> = _items

    fun load() {
        viewModelScope.launch { _items.value = repo.history() }
    }

    fun toggleFavorite(item: Conversion) {
        viewModelScope.launch {
            repo.toggleFavorite(item.id, item.isFavorite)
            load()
        }
    }
}
