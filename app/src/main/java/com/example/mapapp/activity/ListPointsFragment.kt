package com.example.mapapp.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mapapp.R
import com.example.mapapp.adapter.OnInteractionListener
import com.example.mapapp.adapter.PointAdapter
import com.example.mapapp.databinding.FragmentListPointsBinding
import com.example.mapapp.dto.UserPoint
import com.example.mapapp.viewmodel.PointViewModel

class ListPointsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentListPointsBinding.inflate(
            inflater,
            container,
            false
        )

        val pointViewModel: PointViewModel by activityViewModels()

        val adapter = PointAdapter(object : OnInteractionListener {
            override fun onRemove(point: UserPoint) {
                pointViewModel.removeById(point.id)
            }

            override fun onEdit(point: UserPoint) {
                findNavController().navigate(
                    R.id.action_pointFragment_to_pointEditFragment,
                    Bundle().apply { putString(NAME, point.name) })
                pointViewModel.edit(point)
            }

            override fun onPoint(point: UserPoint) {
                findNavController().navigate(
                    R.id.action_pointFragment_to_mapFragment,
                    Bundle().apply {
                        putDouble(LATITUDE, point.point.latitude)
                        putDouble(LONGITUDE, point.point.longitude)
                    })
            }

        })

        binding.recycler.adapter = adapter

        pointViewModel.data.observe(viewLifecycleOwner) { data ->
            adapter.submitList(data)
            println(data)
        }

        return binding.root
    }

    companion object {
        const val NAME = "NAME"
        const val LATITUDE = "LATITUDE"
        const val LONGITUDE = "LONGITUDE"
    }
}