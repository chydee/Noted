package com.chydee.notekeeper.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chydee.notekeeper.R

/**
 * @author: Desmond Ngwuta
 * @email: desmonchidi311@gmail.com
 * @created: 03/01/2020
 */


class StartFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

}