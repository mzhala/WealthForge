package com.example.wealthforge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.NumberPicker
import android.widget.Spinner
import java.util.*

// Optional: use constants if passing parameters
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BudgetFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Optional: update toolbar title if needed
        (activity as? MainActivity)?.updateToolbarTitle("Budget")

        val monthSpinner: Spinner = view.findViewById(R.id.monthSpinner)
        val yearSpinner: Spinner = view.findViewById(R.id.yearSpinner)
        val categorySpinner: Spinner = view.findViewById(R.id.categorySpinner)
        val numberPicker: NumberPicker = view.findViewById(R.id.budgetAmount)

        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        val current = Calendar.getInstance()
        val currentMonth = current.get(Calendar.MONTH)
        val currentYear = current.get(Calendar.YEAR)

        val years = (currentYear..(currentYear + 5)).map { it.toString() }

        monthSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)
        yearSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)

        monthSpinner.setSelection(currentMonth)
        yearSpinner.setSelection(0)

        numberPicker.minValue = 0
        numberPicker.value = 1000
        numberPicker.wrapSelectorWheel = false

        val categories = listOf("Rent", "Groceries", "Transport", "Entertainment")
        categorySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.setSelection(0)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BudgetFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
