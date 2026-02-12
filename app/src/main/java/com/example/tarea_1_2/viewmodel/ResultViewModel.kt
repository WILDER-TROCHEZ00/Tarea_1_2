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

class ResultViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CambioRepository(CambioDao(AppDbHelper(app)))

    private val _item = MutableStateFlow<Conversion?>(null)
    val item: StateFlow<Conversion?> = _item

    fun load(id: Long) {
        viewModelScope.launch {
            _item.value = repo.getConversion(id)
        }
    }
}
