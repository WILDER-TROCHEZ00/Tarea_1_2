package com.example.tarea_1_2.data.model

data class Rate(
    val id: Long,
    val fromCode: String,
    val toCode: String,
    val rate: Double,
    val isCustom: Boolean,
    val isFavorite: Boolean
)
