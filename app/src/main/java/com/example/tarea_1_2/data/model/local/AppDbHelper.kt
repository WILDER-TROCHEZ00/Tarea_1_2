package com.example.tarea_1_2.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE rates(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                from_code TEXT NOT NULL,
                to_code TEXT NOT NULL,
                rate REAL NOT NULL,
                is_custom INTEGER NOT NULL DEFAULT 0,
                is_favorite INTEGER NOT NULL DEFAULT 0,
                UNIQUE(from_code, to_code)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE conversions(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                from_code TEXT NOT NULL,
                to_code TEXT NOT NULL,
                amount REAL NOT NULL,
                result REAL NOT NULL,
                rate REAL NOT NULL,
                date INTEGER NOT NULL,
                is_favorite INTEGER NOT NULL DEFAULT 0
            )
        """.trimIndent())

        // Precarga (ejemplos: ajústalas a tu tarea)
        seedRates(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Para la tarea, simple:
        db.execSQL("DROP TABLE IF EXISTS rates")
        db.execSQL("DROP TABLE IF EXISTS conversions")
        onCreate(db)
    }

    private fun seedRates(db: SQLiteDatabase) {
        // CA -> USD (ejemplos)
        insertRate(db, "HNL", "USD", 0.040)
        insertRate(db, "GTQ", "USD", 0.13)
        insertRate(db, "NIO", "USD", 0.027)
        insertRate(db, "CRC", "USD", 0.0019)
        insertRate(db, "PAB", "USD", 1.0)
        insertRate(db, "USD", "USD", 1.0)
    }

    private fun insertRate(db: SQLiteDatabase, from: String, to: String, rate: Double) {
        db.execSQL(
            "INSERT OR IGNORE INTO rates(from_code,to_code,rate,is_custom,is_favorite) VALUES(?,?,?,?,?)",
            arrayOf(from, to, rate, 0, 0)
        )
    }

    companion object {
        private const val DB_NAME = "cambio_db.sqlite"
        private const val DB_VERSION = 1
    }
}
