package com.example.mapapp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.mapapp.entity.PointEntity

@Dao
interface PointDao {
    @Query("SELECT * FROM PointEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PointEntity>>

    @Upsert
    fun save(point: PointEntity): Long

    @Query("DELETE FROM PointEntity WHERE id = :id")
    fun removeById(id: Long)
}