package com.example.mapapp.dto

import com.yandex.mapkit.geometry.Point

data class UserPoint(
    val id: Long,
    val name: String,
    val point: Point
)
