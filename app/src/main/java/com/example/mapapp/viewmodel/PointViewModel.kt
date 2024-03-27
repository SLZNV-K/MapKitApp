package com.example.mapapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.mapapp.db.AppDb
import com.example.mapapp.dto.UserPoint
import com.example.mapapp.repository.PointRepository
import com.example.mapapp.repository.PointRepositoryImpl
import com.yandex.mapkit.geometry.Point

private val empty = UserPoint(
    id = 0,
    name = "",
    point = Point(0.0, 0.0)
)

class PointViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PointRepository = PointRepositoryImpl(
        AppDb.getInstance(context = application).pointDao()
    )
    val data = repository.getAll()
    private val edited = MutableLiveData(empty)

    fun save() {
        edited.value?.let {
            repository.save(it)
        }
        edited.value = empty
    }

    fun edit(point: UserPoint) {
        edited.value = point
    }

    fun changeName(content: String) {
        val text = content.trim()
        if (edited.value?.name == text) {
            return
        }
        edited.value = edited.value?.copy(name = text)
    }

    fun changeLocation(newPoint: Point) {
        if (edited.value?.point == newPoint) return
        edited.value = edited.value?.copy(point = newPoint)
    }

    fun removeById(id: Long) = repository.removeById(id)

    fun cancel() {
        edited.value = empty
    }
}