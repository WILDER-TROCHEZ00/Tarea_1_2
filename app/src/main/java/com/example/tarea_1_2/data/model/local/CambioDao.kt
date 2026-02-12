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
}
