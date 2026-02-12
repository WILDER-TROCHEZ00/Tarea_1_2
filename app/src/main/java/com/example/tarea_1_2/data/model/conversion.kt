package com.example.tarea_1_2.data.model

data class Conversion(
    val id: Long,
    val fromCode: String,
    val toCode: String,
    val amount: Double,
    val result: Double,
    val rate: Double,
    val date: Long,
    val isFavorite: Boolean
)
