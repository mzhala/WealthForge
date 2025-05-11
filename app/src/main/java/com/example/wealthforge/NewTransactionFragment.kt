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
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NewTransactionFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NewTransactionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Update toolbar title
        (activity as? MainActivity)?.updateToolbarTitle("New Transaction")

        // category name spinner
        val categorySpinner: Spinner = view.findViewById(R.id.categorySpinner)
        val categories = listOf("Rent", "Groceries", "Transport", "Entertainment")
        categorySpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        categorySpinner.setSelection(0)

        // month and year spinner
        val monthSpinner: Spinner = view.findViewById(R.id.monthSpinner)
        val yearSpinner: Spinner = view.findViewById(R.id.yearSpinner)

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

        // recycler view - recent transactions
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        val transactionRecords = listOf(
            TransactionRecordItem("Spotify", "27/03/2025", "R10 000.00", R.drawable.spotify),
            TransactionRecordItem("Grocery", "28/03/2025", "R7 500.00", R.drawable.grocery),
            TransactionRecordItem("Transport", "29/03/2025", "R1 200.00", R.drawable.transport)
        )

        recyclerView.layoutManager = LinearLayoutManager(context) // Set the layout manager
        recyclerView.adapter = TransactionRecordAdapter(transactionRecords) // Set the adapter to bind data
    }
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
        return inflater.inflate(R.layout.fragment_new_transaction, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NewTransactionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NewTransactionFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}