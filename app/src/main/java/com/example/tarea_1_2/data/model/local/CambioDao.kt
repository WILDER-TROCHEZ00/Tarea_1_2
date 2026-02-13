package com.example.tarea_1_2.data.local

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import com.example.tarea_1_2.data.model.Conversion
import com.example.tarea_1_2.data.model.Rate

class CambioDao(private val dbHelper: AppDbHelper) {

    private fun readDb(): SQLiteDatabase = dbHelper.readableDatabase
    private fun writeDb(): SQLiteDatabase = dbHelper.writableDatabase

    fun getRate(from: String, to: String): Rate? {
        val db = readDb()
        val c = db.rawQuery(
            "SELECT id, from_code, to_code, rate, is_custom, is_favorite FROM rates WHERE from_code=? AND to_code=? LIMIT 1",
            arrayOf(from, to)
        )
        c.use {
            if (!it.moveToFirst()) return null
            return Rate(
                id = it.getLong(0),
                fromCode = it.getString(1),
                toCode = it.getString(2),
                rate = it.getDouble(3),
                isCustom = it.getInt(4) == 1,
                isFavorite = it.getInt(5) == 1
            )
        }
    }

    fun insertConversion(from: String, to: String, amount: Double, result: Double, rate: Double): Long {
        val db = writeDb()
        val values = ContentValues().apply {
            put("from_code", from)
            put("to_code", to)
            put("amount", amount)
            put("result", result)
            put("rate", rate)
            put("date", System.currentTimeMillis())
            put("is_favorite", 0)
        }
        return db.insertOrThrow("conversions", null, values)
    }

    fun getConversionById(id: Long): Conversion? {
        val db = readDb()
        val c = db.rawQuery(
            "SELECT id, from_code, to_code, amount, result, rate, date, is_favorite FROM conversions WHERE id=? LIMIT 1",
            arrayOf(id.toString())
        )
        c.use {
            if (!it.moveToFirst()) return null
            return Conversion(
                id = it.getLong(0),
                fromCode = it.getString(1),
                toCode = it.getString(2),
                amount = it.getDouble(3),
                result = it.getDouble(4),
                rate = it.getDouble(5),
                date = it.getLong(6),
                isFavorite = it.getInt(7) == 1
            )
        }
    }

    fun listHistory(): List<Conversion> {
        val db = readDb()
        val c = db.rawQuery(
            "SELECT id, from_code, to_code, amount, result, rate, date, is_favorite FROM conversions ORDER BY date DESC",
            null
        )
        val out = mutableListOf<Conversion>()
        c.use {
            while (it.moveToNext()) {
                out += Conversion(
                    id = it.getLong(0),
                    fromCode = it.getString(1),
                    toCode = it.getString(2),
                    amount = it.getDouble(3),
                    result = it.getDouble(4),
                    rate = it.getDouble(5),
                    date = it.getLong(6),
                    isFavorite = it.getInt(7) == 1
                )
            }
        }
        return out
    }

    fun setConversionFavorite(id: Long, fav: Boolean) {
        val db = writeDb()
        db.execSQL(
            "UPDATE conversions SET is_favorite=? WHERE id=?",
            arrayOf(if (fav) 1 else 0, id)
        )
    }

    fun listRates(): List<Rate> {
        val db = readDb()
        val c = db.rawQuery(
            "SELECT id, from_code, to_code, rate, is_custom, is_favorite FROM rates " +
                    "ORDER BY is_favorite DESC, from_code ASC, to_code ASC"
            ,
            null
        )
        val out = mutableListOf<Rate>()
        c.use {
            while (it.moveToNext()) {
                out += Rate(
                    id = it.getLong(0),
                    fromCode = it.getString(1),
                    toCode = it.getString(2),
                    rate = it.getDouble(3),
                    isCustom = it.getInt(4) == 1,
                    isFavorite = it.getInt(5) == 1
                )
            }
        }
        return out
    }

    /**
     * Upsert seguro (sin cambiar id):
     * - Si existe (from_code,to_code): UPDATE rate + is_custom=1
     * - Si no existe: INSERT (is_custom=1)
     * Retorna el id de la tasa.
     */
    fun upsertRate(from: String, to: String, rate: Double): Long {
        val db = writeDb()
        db.beginTransaction()
        try {
            val valuesUpdate = ContentValues().apply {
                put("rate", rate)
                put("is_custom", 1)
            }

            val updated = db.update(
                "rates",
                valuesUpdate,
                "from_code=? AND to_code=?",
                arrayOf(from, to)
            )

            val id = if (updated > 0) {
                // Obtener id existente
                val c = db.rawQuery(
                    "SELECT id FROM rates WHERE from_code=? AND to_code=? LIMIT 1",
                    arrayOf(from, to)
                )
                c.use {
                    if (it.moveToFirst()) it.getLong(0) else -1L
                }
            } else {
                // Insertar nueva
                val valuesInsert = ContentValues().apply {
                    put("from_code", from)
                    put("to_code", to)
                    put("rate", rate)
                    put("is_custom", 1)
                    put("is_favorite", 0)
                }
                db.insertOrThrow("rates", null, valuesInsert)
            }

            db.setTransactionSuccessful()
            return id
        } finally {
            db.endTransaction()
        }
    }

    fun setRateFavorite(id: Long, fav: Boolean) {
        val db = writeDb()
        db.execSQL(
            "UPDATE rates SET is_favorite=? WHERE id=?",
            arrayOf(if (fav) 1 else 0, id)
        )
    }

    fun toggleRateFavorite(id: Long, current: Boolean) {
        setRateFavorite(id, !current)
    }

}
