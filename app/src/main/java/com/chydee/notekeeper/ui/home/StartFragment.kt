package com.chydee.notekeeper.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.chydee.notekeeper.R
import com.chydee.notekeeper.databinding.FragmentStartBinding

/**
 * @author: Desmond Ngwuta
 * @email: desmonchidi311@gmail.com
 * @created: 03/01/2020
 */


class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentStartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        binding.getStartedBtn.setOnClickListener {
            findNavController().navigate(R.id.action_startFragment_to_homeFragment)
        }
    }

}