package com.chydee.notekeeper.ui.trash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.chydee.notekeeper.R
import com.chydee.notekeeper.data.model.Trash
import com.chydee.notekeeper.databinding.TrashFragmentBinding
import com.chydee.notekeeper.ui.main.BaseFragment
import com.chydee.notekeeper.utils.SpacesItemDecoration
import com.chydee.notekeeper.utils.ViewModelFactory
import com.chydee.notekeeper.utils.toNote

class TrashFragment : BaseFragment() {


    private lateinit var viewModel: TrashViewModel
    private lateinit var viewModelFactory: ViewModelFactory

    private lateinit var binding: TrashFragmentBinding

    private lateinit var adapter: TrashAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = TrashFragmentBinding.inflate(inflater)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModelFactory = ViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory).get(TrashViewModel::class.java)
        binding.trashViewModel = viewModel
        binding.lifecycleOwner = this
        showNavigationIcon()
        loadTrash()

        binding.clearTrash.setOnClickListener {
            viewModel.deleteForever()
            adapter.updateTrash(emptyList())
            binding.emptyTrashState.visibility = View.VISIBLE
        }
    }

    private fun loadTrash() {
        viewModel.getDeletedNotes()
        viewModel.deletedNotes.observe(viewLifecycleOwner, { exNote ->
            if (exNote != null) {
                if (exNote.isNotEmpty()) {
                    binding.emptyTrashState.visibility = View.GONE
                    setupRV(exNote)
                } else {
                    binding.emptyTrashState.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setupRV(list: List<Trash>) {
        adapter = TrashAdapter()
        val manager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = manager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(SpacesItemDecoration(14))
        adapter.trashes = list as ArrayList<Trash>

        adapter.setOnClickListener(object : TrashAdapter.OnItemClickListener {
            override fun onTrashClick(trash: Trash) {
                //findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(note))
                snackBarWithAction("Can't open items in trash", getString(R.string.restore)) {
                    restoreNotes(trash)
                }
            }
        })
    }

    private fun restoreNotes(trash: Trash) {
        val trashes: ArrayList<Trash> = adapter.trashes
        viewModel.insertNote(trash.toNote())
        viewModel.removeTrash(trash)

        trashes.remove(trash)
        adapter.notifyDataSetChanged()
    }

}