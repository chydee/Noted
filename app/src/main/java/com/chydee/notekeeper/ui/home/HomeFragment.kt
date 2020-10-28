package com.chydee.notekeeper.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.HomeFragmentBinding
import com.chydee.notekeeper.ui.NoteAdapter
import com.chydee.notekeeper.utils.SpacesItemDecoration
import com.chydee.notekeeper.utils.ViewModelFactory
import com.google.android.material.appbar.MaterialToolbar


class HomeFragment : Fragment() {

    private lateinit var binding: HomeFragmentBinding

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var adapter: NoteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = HomeFragmentBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        binding.homeViewModel = viewModel
        binding.lifecycleOwner = this

        val appbar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appbar.navigationIcon = null

        viewModel.getNotes()
        setupListener()
        setupRV()
    }

    private fun setupListener() {
        binding.floatingActionButton.setOnClickListener { view: View ->
            val action = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(null)
            view.findNavController().navigate(action)
        }
    }


    private fun setupRV() {
        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = manager
        adapter = NoteAdapter(NoteAdapter.OnClickListener { findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(it)) })
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(SpacesItemDecoration(14))
        viewModel.notes.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it.isNotEmpty()) {
                    binding.emptyNotesState.visibility = View.GONE
                    adapter.submitList(it)
                } else {
                    binding.emptyNotesState.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setupViewModel() {
        viewModelFactory = ViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
    }
}
