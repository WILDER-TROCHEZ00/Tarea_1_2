package com.example.tarea_1_2.data.repository

import com.example.tarea_1_2.data.local.CambioDao
import com.example.tarea_1_2.data.model.Conversion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CambioRepository(private val dao: CambioDao) {

    suspend fun convertAndSave(amount: Double, from: String, to: String): Long = withContext(Dispatchers.IO) {
        val rate = if (from == to) {
            1.0
        } else {
            dao.getRate(from, to)?.rate
                ?: throw IllegalStateException("No existe tasa para $from → $to")
        }
        val result = amount * rate
        dao.insertConversion(from, to, amount, result, rate)
    }

    suspend fun getConversion(id: Long): Conversion? = withContext(Dispatchers.IO) {
        dao.getConversionById(id)
    }

    suspend fun history(): List<Conversion> = withContext(Dispatchers.IO) {
        dao.listHistory()
    }

    suspend fun toggleFavorite(id: Long, current: Boolean) = withContext(Dispatchers.IO) {
        dao.setConversionFavorite(id, !current)
    }
}
