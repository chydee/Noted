package com.chydee.notekeeper.home

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.chydee.notekeeper.R
import com.chydee.notekeeper.database.NoteDatabase
import com.chydee.notekeeper.databinding.HomeFragmentBinding


class HomeFragment : Fragment() {
    private val isChecked = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding: HomeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = NoteDatabase.getInstance(application).noteDatabaseDao
        val homeViewModelFactory = HomeViewModelFactory(dataSource, application)

        val viewModel: HomeViewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)

        binding.homeViewModel = viewModel

        binding.lifecycleOwner = this

        setHasOptionsMenu(true)

        val manager = GridLayoutManager(activity, 1)
        binding.recyclerView.layoutManager = manager

        binding.recyclerView.adapter = NoteAdapter(NoteAdapter.OnClickListener { viewModel.displayNoteDetails(it) })

        viewModel.navigateToSelectedNote.observe(viewLifecycleOwner, Observer {
            if (null != it) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(it))
            }
        })

        binding.floatingActionButton.setOnClickListener { view: View ->
            val action = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(null)
            view.findNavController().navigate(action)
        }

        // Note that the Toolbar defined in the layout has the id "my_toolbar"
        //setSupportActionBar(findViewById(R.id.my_toolbar))

        return binding.root
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val checkable = menu.findItem(R.id.toggle_list_display)
        checkable.isChecked = isChecked
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

}
