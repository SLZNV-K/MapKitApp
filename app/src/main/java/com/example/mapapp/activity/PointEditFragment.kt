package com.example.mapapp.activity

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mapapp.R
import com.example.mapapp.activity.ListPointsFragment.Companion.NAME
import com.example.mapapp.databinding.FragmentPointEditBinding
import com.example.mapapp.viewmodel.PointViewModel

class PointEditFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPointEditBinding.inflate(
            inflater,
            container,
            false
        )
        val viewModel: PointViewModel by activityViewModels()

        with(binding) {
            inputName.setText(arguments?.getString(NAME))
            save.setOnClickListener {
                if (!inputName.text.isNullOrBlank()) {
                    val content = inputName.text.toString()
                    viewModel.changeName(content)
                    viewModel.save()
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(requireActivity(), R.string.empty_text_error, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    AlertDialog.Builder(requireActivity()).apply {
                        setTitle(getString(R.string.cancel))
                        setPositiveButton(getString(R.string.yes)) { _, _ ->
                            viewModel.cancel()
                            findNavController().navigateUp()
                        }
                        setNegativeButton(getString(R.string.no)) { _, _ -> }
                        setCancelable(true)
                    }.create().show()
                }
            }
        )

        return binding.root
    }

}