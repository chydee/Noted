package com.chydee.notekeeper.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chydee.notekeeper.R
import com.chydee.notekeeper.data.model.Note
import com.chydee.notekeeper.databinding.HomeFragmentBinding
import com.chydee.notekeeper.ui.NoteAdapter
import com.chydee.notekeeper.ui.main.BaseFragment
import com.chydee.notekeeper.utils.MyLookup
import com.chydee.notekeeper.utils.SpacesItemDecoration
import com.chydee.notekeeper.utils.ViewModelFactory
import com.google.android.material.appbar.MaterialToolbar


class HomeFragment : BaseFragment() {

    private lateinit var binding: HomeFragmentBinding

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var adapter: NoteAdapter

    private lateinit var deleteList: ArrayList<Note>
    private var tracker: SelectionTracker<Long>? = null

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

        deleteList = arrayListOf()
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
        adapter = NoteAdapter()
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

        tracker = SelectionTracker.Builder<Long>(
                "mySelection",
                binding.recyclerView,
                StableIdKeyProvider(binding.recyclerView),
                MyLookup(binding.recyclerView),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
        ).build()

        adapter.tracker = tracker

        tracker?.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                val selected: Int? = tracker?.selection?.size()
                if (selected != null && selected > 0) {
                    with(binding) {
                        selectedOptions.visibility = View.VISIBLE
                        selectedCount.text = getString(R.string.default_selection_count, selected.toString())
                    }
                }
                /* val notes: List<Note> = adapter.currentList
                if (selectionIds != null) {
                    for (i in 0..selectionIds.size()) {
                        deleteList.add(notes[i])
                    }
                    Log.d("Selected ", tracker?.selection.toString())
                    Log.d("Selected ", "First Item to be deleted ${deleteList[0].noteDetail}")
                }
            */
            }
        })

        binding.cancelSelection.setOnClickListener {
            val isCleared = tracker?.clearSelection()
            binding.selectedOptions.visibility = if (isCleared!!) View.GONE else View.VISIBLE
        }

        adapter.setOnClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onNoteClick(note: Note) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(note))
            }
        })
    }

    private fun setupViewModel() {
        viewModelFactory = ViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
    }
}
