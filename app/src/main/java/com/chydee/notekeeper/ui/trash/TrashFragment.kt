package com.chydee.notekeeper.ui.trash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chydee.notekeeper.R
import com.chydee.notekeeper.data.model.Trash
import com.chydee.notekeeper.databinding.TrashFragmentBinding
import com.chydee.notekeeper.ui.main.BaseFragment
import com.chydee.notekeeper.utils.SpacesItemDecoration
import com.chydee.notekeeper.utils.ext.hide
import com.chydee.notekeeper.utils.ext.show
import com.chydee.notekeeper.utils.toNote
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrashFragment : BaseFragment() {

    private val viewModel: TrashViewModel by viewModels()

    private lateinit var binding: TrashFragmentBinding

    private lateinit var adapter: TrashAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TrashFragmentBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.trashViewModel = viewModel
        binding.lifecycleOwner = this
        hideNavigationIcon()
        showNavigationIcon()
        loadTrash()
        handleOnClickEvents()
    }

    private fun handleOnClickEvents() {
        binding.clearTrash.setOnClickListener {
            viewModel.deleteForever()
            adapter.updateTrash(emptyList())
            binding.emptyTrashState.show()
            binding.trashDisclaimer.hide()
        }
    }

    /**
     * Get Deleted Notes in the TrashTable or Entity
     * and display then with the RecyclerView
     */
    private fun loadTrash() {
        viewModel.getDeletedNotes()
        viewModel.deletedNotes.observe(
            viewLifecycleOwner,
            { exNote ->
                if (exNote != null) {
                    if (exNote.isNotEmpty()) {
                        showAndHideViewsWhenTrashIsNotEmpty()
                        setupRV(exNote)
                    } else {
                        hideAndShowViewsWhenTrashIsEmpty()
                    }
                }
            }
        )
    }

    /**
     *  Set up RecyclerView and link to the TrashAdapter
     */
    private fun setupRV(list: List<Trash>) {
        adapter = TrashAdapter()
        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(SpacesItemDecoration(14))
        adapter.trashes = list as ArrayList<Trash>

        adapter.setOnClickListener(object : TrashAdapter.OnItemClickListener {
            override fun onTrashClick(trash: Trash) {
                // findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(note))
                snackBarWithAction("Can't open items in trash", getString(R.string.restore)) {
                    restoreNotes(trash)
                }
            }
        })
    }

    /**
     *  Restore Notes by removing the notes from the TrashTable and inserting in
     *  the NoteTable or Entity and notify Adapter that the Data set has changed
     */
    private fun restoreNotes(trash: Trash) {
        val trashes: ArrayList<Trash> = adapter.trashes
        viewModel.insertNote(trash.toNote())
        viewModel.removeTrash(trash)
        trashes.remove(trash)
        adapter.notifyDataSetChanged()
    }

    /**
     *  Show the RecyclerView, Trash Disclaimer and Hide The EmptyState for the trash when the trash is not empty
     */
    private fun showAndHideViewsWhenTrashIsNotEmpty() {
        binding.trashDisclaimer.show()
        binding.recyclerView.show()
        binding.emptyTrashState.hide()
    }

    /**
     *  Show the EmptyState for the trash, and Hide the RecyclerView Trash Disclaimer when the trash is  empty
     */
    private fun hideAndShowViewsWhenTrashIsEmpty() {
        binding.clearTrash.isEnabled = false
        binding.trashDisclaimer.hide()
        binding.recyclerView.hide()
        binding.emptyTrashState.show()
    }
}
