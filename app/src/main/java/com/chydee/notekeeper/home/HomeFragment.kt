package com.chydee.notekeeper.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    private lateinit var viewModel: HomeViewModel

    private lateinit var homeViewModelFactory: HomeViewModelFactory

    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)

        binding.homeViewModel = viewModel

        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application

        val dataSource = NoteDatabase.getInstance(application).noteDatabaseDao

        homeViewModelFactory = HomeViewModelFactory(dataSource, application)

        val manager = GridLayoutManager(activity, 1)
        binding.recyclerView.layoutManager = manager

        binding.recyclerView.adapter = NoteAdapter(NoteAdapter.OnClickListener { viewModel.displayNoteDetails(it) })

        viewModel.navigateToSelectedNote.observe(this, Observer {
            if (null != it) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(it))
            }
        })

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, homeViewModelFactory).get(HomeViewModel::class.java)

        binding.floatingActionButton.setOnClickListener { view: View ->
            val action = HomeFragmentDirections.actionHomeFragmentToEditNoteFragment(null)
            view.findNavController().navigate(action)
        }
    }

}
