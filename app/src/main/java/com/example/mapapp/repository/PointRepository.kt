package com.example.mapapp.repository

import androidx.lifecycle.LiveData
import com.example.mapapp.dto.UserPoint

interface PointRepository {
    fun getAll(): LiveData<List<UserPoint>>
    fun save(point: UserPoint)
    fun removeById(id: Long)
}