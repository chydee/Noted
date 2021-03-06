package com.chydee.notekeeper.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
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
import com.chydee.notekeeper.ui.bottomsheets.UnlockNoteBottomSheet
import com.chydee.notekeeper.ui.main.BaseFragment
import com.chydee.notekeeper.utils.MyLookup
import com.chydee.notekeeper.utils.SpacesItemDecoration
import com.chydee.notekeeper.utils.ext.hide
import com.chydee.notekeeper.utils.ext.show
import com.chydee.notekeeper.utils.toTrash
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment(), UnlockNoteBottomSheet.OnClickListener {

    private var binding: HomeFragmentBinding? = null

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var adapter: NoteAdapter

    private lateinit var deleteList: ArrayList<Trash>
    private var tracker: SelectionTracker<Long>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.homeViewModel = viewModel
        binding?.lifecycleOwner = this
        hideNavigationIcon()
        handleOnClickEvents()
        setUpNotesAdapter()
    }

    private fun filterNoteList() {
        binding?.searchNote?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
                adapter.filter.filter(s)
            }
        })
    }

    private fun handleOnClickEvents() {
        binding?.floatingActionButton?.setOnClickListener { view: View ->
            val action = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(null)
            view.findNavController().navigate(action)
        }
        binding?.cancelSelection?.setOnClickListener {
            clearSelection()
        }

        binding?.deleteSelected?.setOnClickListener {
            moveToTrash()
            clearSelection()
        }
    }

    /**
     *  Set up the RecyclerView and link with the adapter
     */
    private fun setUpNotesAdapter() {
        viewModel.getNotes()

        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding?.recyclerView?.layoutManager = manager
        adapter = NoteAdapter()
        binding?.recyclerView?.adapter = adapter
        binding?.recyclerView?.addItemDecoration(SpacesItemDecoration(14))
        setUpObservers()
        buildSelectionTracker()
        adapter.setOnClickListener(object : NoteAdapter.OnItemClickListener {
            override fun onNoteClick(note: Note) {
                if (note.isLocked) {
                    showBottomSheet(note)
                } else {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(note))
                }
            }
        })
    }

    private fun setUpObservers() {
        viewModel.notes.observe(
            viewLifecycleOwner,
            {
                if (it != null) {
                    if (it.isNotEmpty()) {
                        binding?.emptyNotesState?.hide()
                        adapter.setNotes(it as ArrayList)
                        adapter.notifyDataSetChanged()

                        filterNoteList()
                    } else {
                        binding?.emptyNotesState?.show()
                    }
                }
            }
        )
    }

    private fun buildSelectionTracker() {
        tracker = binding?.recyclerView?.let { rv ->
            SelectionTracker.Builder(
                "mySelection",
                rv,
                StableIdKeyProvider(rv),
                MyLookup(rv),
                StorageStrategy.createLongStorage()
            ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything()
            ).build()
        }
        adapter.tracker = tracker
        observeTracker()
    }

    /**
     *  Clear or cancel selection amd return back to the initial state
     *  by setting the visibility of the selectedOptions View to View.GONE
     *  when isCleared is true
     */
    private fun clearSelection() {
        val isCleared = tracker?.clearSelection()
        binding?.selectedOptions?.visibility = if (isCleared == true) View.GONE else View.VISIBLE
    }

    /**
     *  Listen to the RecyclerView Selection Tracker and update the selectedCount TextView with the number selected
     */
    private fun observeTracker() {
        tracker?.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                super.onSelectionChanged()
                val selected: Int? = tracker?.selection?.size()
                if (selected != null && selected > 0) {
                    with(binding) {
                        binding?.selectedOptions?.show()
                        binding?.selectedCount?.text = getString(R.string.default_selection_count, selected.toString())
                    }
                }
            }
        })
    }

    /**
     *  Move selected Note items to trash and remove or delete them from the NoteTable or Entity
     */
    private fun moveToTrash() {
        deleteList = arrayListOf()
        val notes: ArrayList<Note> = adapter.getNotes()
        val newNotes: ArrayList<Note> = arrayListOf()
        tracker?.selection?.forEach {
            if (tracker?.isSelected(it) == true) {
                deleteList.add(notes[it.toInt()].toTrash())
                newNotes.add(notes[it.toInt()])
                viewModel.deleteNote(notes[it.toInt()])
            }
        }
        notes.removeAll(newNotes)
        viewModel.addToTrash(deleteList)
        adapter.notifyDataSetChanged()
        setUpObservers()

        // Show a snack bar indicating the number of notes deleted and an options to undo
        // This action
        snackBarWithAction("${deleteList.size} item(s) removed", getString(R.string.undo)) {
            notes.addAll(undoDelete(newNotes))
            adapter.notifyDataSetChanged()
            showSnackBar("Note(s) restored")
        }
    }

    private fun undoDelete(notes: List<Note>): List<Note> {
        if (notes.isNotEmpty()) {
            notes.forEach {
                viewModel.insertNote(it)
                viewModel.removeFromTrash(it.toTrash())
            }
        }
        return notes
    }

    private fun showBottomSheet(note: Note) {
        val unlockSheet = UnlockNoteBottomSheet.instance(this)
        val bundle = Bundle()
        bundle.putParcelable("Note", note)
        unlockSheet.arguments = bundle
        unlockSheet.show(childFragmentManager, "UnlockNote")
    }

    override fun onNoteUnlocked(note: Note) {
        viewModel.updateNote(note)
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(note))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        tracker = null
    }
}
