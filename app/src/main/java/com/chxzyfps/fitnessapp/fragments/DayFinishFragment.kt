package com.chxzyfps.fitnessapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.chxzyfps.fitnessapp.R
import com.chxzyfps.fitnessapp.databinding.DayFinishBinding
import com.chxzyfps.fitnessapp.utils.FragmentManager
import pl.droidsonroids.gif.GifDrawable


class DayFinishFragment : Fragment() {
    private lateinit var binding: DayFinishBinding
    private var ab: ActionBar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DayFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imMain.setImageDrawable(GifDrawable((activity as AppCompatActivity).assets, "relax.gif"))
        binding.bDone.setOnClickListener {
            FragmentManager.setFragment(DaysFragment.newInstance(),
                activity as AppCompatActivity)
        }
        ab = (activity as AppCompatActivity).supportActionBar
        val title = (activity as AppCompatActivity).getString(R.string.finish_training_message)
        ab?.title = title

    }

    companion object {
        @JvmStatic
        fun newInstance() = DayFinishFragment()
    }
}