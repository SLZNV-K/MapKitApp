package com.example.mapapp.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mapapp.dto.UserPoint
import com.yandex.mapkit.geometry.Point

@Entity
class PointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,

    ) {
    fun toDto(): UserPoint = UserPoint(id = id, name = name, point = Point(latitude, longitude))

    companion object {
        fun fromDto(dto: UserPoint): PointEntity = with(dto) {
            PointEntity(
                id = id,
                name = name,
                latitude = point.latitude,
                longitude = point.longitude
            )
        }
    }
}