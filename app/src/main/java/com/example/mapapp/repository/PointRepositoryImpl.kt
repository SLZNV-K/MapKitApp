package com.example.mapapp.repository

import androidx.lifecycle.map
import com.example.mapapp.dao.PointDao
import com.example.mapapp.dto.UserPoint
import com.example.mapapp.entity.PointEntity.Companion.fromDto

class PointRepositoryImpl(
    private val pointDao: PointDao
) : PointRepository {

    override fun getAll() = pointDao.getAll().map { list ->
        list.map {
            it.toDto()
        }
    }

    override fun save(point: UserPoint) {
        pointDao.save(fromDto(point))
    }

    override fun removeById(id: Long) {
        pointDao.removeById(id)
    }
}