package com.chydee.notekeeper.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.chydee.notekeeper.data.model.Trash
import com.chydee.notekeeper.databinding.HomeFragmentBinding
import com.chydee.notekeeper.ui.NoteAdapter
import com.chydee.notekeeper.ui.main.BaseFragment
import com.chydee.notekeeper.utils.MyLookup
import com.chydee.notekeeper.utils.SpacesItemDecoration
import com.chydee.notekeeper.utils.ViewModelFactory
import com.chydee.notekeeper.utils.toTrash
import com.google.android.material.appbar.MaterialToolbar


class HomeFragment : BaseFragment() {

    private lateinit var binding: HomeFragmentBinding

    private lateinit var viewModel: HomeViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var adapter: NoteAdapter

    private lateinit var deleteList: ArrayList<Trash>
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
        val appbar = requireActivity().findViewById<MaterialToolbar>(R.id.topAppBar)
        appbar.navigationIcon = null
        setupListener()
        setupRV()
    }


    private fun setupListener() {
        binding.floatingActionButton.setOnClickListener { view: View ->
            val action = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(null)
            view.findNavController().navigate(action)
        }
        binding.cancelSelection.setOnClickListener {
            clearSelection()
        }

        binding.deleteSelected.setOnClickListener {
            moveToTrash()
            clearSelection()
        }
    }


    private fun setupRV() {
        viewModel.getNotes()

        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = manager
        adapter = NoteAdapter()
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(SpacesItemDecoration(14))
        viewModel.notes.observe(viewLifecycleOwner, {
            if (it != null) {
                if (it.isNotEmpty()) {
                    binding.emptyNotesState.visibility = View.GONE
                    adapter.items = it as ArrayList<Note>
                    adapter.notifyDataSetChanged()
                } else {
                    binding.emptyNotesState.visibility = View.VISIBLE
                }
            }
        })

        tracker = SelectionTracker.Builder(
                "mySelection",
                binding.recyclerView,
                StableIdKeyProvider(binding.recyclerView),
                MyLookup(binding.recyclerView),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
        ).build()

        adapter.tracker = tracker
        observeTracker()


        adapter.setOnClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onNoteClick(note: Note) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(note))
            }
        })
    }

    private fun clearSelection() {
        val isCleared = tracker?.clearSelection()
        binding.selectedOptions.visibility = if (isCleared!!) View.GONE else View.VISIBLE
    }

    private fun observeTracker() {
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
            }
        })
    }

    private fun moveToTrash() {
        deleteList = arrayListOf()
        val notes: ArrayList<Note> = adapter.items
        val newNotes: ArrayList<Note> = arrayListOf()
        tracker?.selection?.forEach {
            if (tracker?.isSelected(it)!!) {
                deleteList.add(notes[it.toInt()].toTrash())
                newNotes.add(notes[it.toInt()])
                viewModel.deleteNote(notes[it.toInt()])
            }
        }
        notes.removeAll(newNotes)
        viewModel.addToTrash(deleteList)
        adapter.notifyDataSetChanged()

        snackBarWithAction("${deleteList.size} item(s) removed", getString(R.string.undo)) {
            Toast.makeText(context, "Remove undone", Toast.LENGTH_LONG).show()
        }

    }

    private fun setupViewModel() {
        viewModelFactory = ViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory).get(HomeViewModel::class.java)
    }
}
