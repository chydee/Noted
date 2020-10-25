package com.chydee.notekeeper.ui.home

import android.app.Application
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
import com.chydee.notekeeper.utils.ViewModelFactory
import java.util.*


class HomeFragment : Fragment() {

    private lateinit var binding: HomeFragmentBinding
    private val isChecked = false
    private lateinit var application: Application

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var adapter: NoteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = HomeFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        binding.homeViewModel = viewModel
        binding.lifecycleOwner = this
        greetUser()
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
        viewModel.getNotes()
        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = manager
        adapter = NoteAdapter(NoteAdapter.OnClickListener { viewModel.displayNoteDetails(it) })
        binding.recyclerView.adapter = adapter
        viewModel.notes.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it.isNotEmpty()) {
                    adapter.submitList(it)
                } else {
                    binding.emptyNotesState.visibility = View.VISIBLE
                }
            }
        })
        viewModel.navigateToSelectedNote.observe(viewLifecycleOwner, {
            if (null != it) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(it))
            }
        })
    }

    private fun greetUser() {
        val c: Calendar = Calendar.getInstance()

        when (c.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> {
                binding.greetings.text = getString(R.string.good_morning)
            }
            in 12..15 -> {
                binding.greetings.text = getString(R.string.good_afternoon)
            }
            in 16..20 -> {
                binding.greetings.text = getString(R.string.good_evening)
            }
            in 21..23 -> {
                binding.greetings.text = getString(R.string.good_night)
            }
        }
    }

    private fun setupViewModel() {
        application = requireActivity().application
        viewModelFactory = ViewModelFactory(application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
    }
}
