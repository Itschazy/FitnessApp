package com.chxzyfps.fitnessapp.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.chxzyfps.fitnessapp.R
import com.chxzyfps.fitnessapp.adapters.DayModel
import com.chxzyfps.fitnessapp.adapters.DaysAdapter
import com.chxzyfps.fitnessapp.adapters.ExerciseModel
import com.chxzyfps.fitnessapp.databinding.FragmentDaysBinding
import com.chxzyfps.fitnessapp.utils.DialogManager
import com.chxzyfps.fitnessapp.utils.FragmentManager
import com.chxzyfps.fitnessapp.utils.MainViewModel

class DaysFragment : Fragment(), DaysAdapter.Listener {
    private lateinit var binding: FragmentDaysBinding
    private var ab: ActionBar? = null
    private val model: MainViewModel by activityViewModels()
    private lateinit var adapter: DaysAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDaysBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.currentDay = 0
        initRcView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        return inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.clear_menu) {
            DialogManager.showDialog(activity as AppCompatActivity,
                R.string.clear_all_training_progress_dialog_message,
                object : DialogManager.Listener {
                    override fun onClick() {
                        model.pref?.edit()?.clear()?.apply()
                        adapter.submitList(fillDaysArray())
                    }
                })
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initRcView() = with(binding) {
        adapter = DaysAdapter(this@DaysFragment)
        ab = (activity as AppCompatActivity).supportActionBar
        ab?.title = getString(R.string.training_days)
        rcViewDays.layoutManager = LinearLayoutManager(activity as AppCompatActivity)
        rcViewDays.adapter = adapter
        adapter.submitList(fillDaysArray())
    }

    private fun fillDaysArray(): ArrayList<DayModel> {
        val tArray = ArrayList<DayModel>()
        var daysDoneCounter = 0
        resources.getStringArray(R.array.day_exercises).forEach {
            model.currentDay++
            val exCounter = it.split(",").size
            val isDone = model.getExerciseCount() == exCounter
            tArray.add(
                DayModel(
                    exercises = it,
                    0,
                    isDone = isDone
                )
            )
        }
        binding.pB.max = tArray.size
        tArray.forEach {
            if (it.isDone) daysDoneCounter++
        }
        binding.pB.progress = daysDoneCounter
        updateRestDaysUI(daysDoneCounter, tArray.size)
        return tArray
    }

    private fun updateRestDaysUI(daysDone: Int, totalDays: Int) = with(binding) {
        val restDaysMessage =
            "${totalDays - daysDone} ${getString(R.string.days)} ${getString(R.string.left)}"
        tvRestDays.text = restDaysMessage
    }

    private fun fillExerciseList(day: DayModel) {
        val tList = ArrayList<ExerciseModel>()
        val exerciseList = resources.getStringArray(R.array.exercise)
        day.exercises.split(",").forEach {
            val exercise = exerciseList[it.toInt()].split("|")
            tList.add(
                ExerciseModel(
                    exercise[0],
                    exercise[1],
                    false,
                    exercise[2]
                )
            )
        }
        model.mutableListExercise.value = tList
    }

    companion object {
        @JvmStatic
        fun newInstance() = DaysFragment()
    }

    override fun onClick(day: DayModel) {
        if (!day.isDone) {
            fillExerciseList(day)
            model.currentDay = day.dayNumber
            FragmentManager.setFragment(
                ExerciseListFragment.newInstance(),
                activity as AppCompatActivity
            )
        } else {
            DialogManager.showDialog(activity as AppCompatActivity,
                R.string.clear_day_training_progress_dialog_message,
                object : DialogManager.Listener {
                    override fun onClick() {
                        model.savePref(day.dayNumber.toString(), 0)
                        fillExerciseList(day)
                        model.currentDay = day.dayNumber
                        FragmentManager.setFragment(
                            ExerciseListFragment.newInstance(),
                            activity as AppCompatActivity
                        )
                    }
                })
        }
    }
}