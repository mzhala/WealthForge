package com.example.wealthforge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TransactionHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TransactionHistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Update toolbar title
        (activity as? MainActivity)?.updateToolbarTitle("Transaction History")

        // month and year spinner
        val startMonthSpinner: Spinner = view.findViewById(R.id.startMonthSpinner)
        val startYearSpinner: Spinner = view.findViewById(R.id.startYearSpinner)

        val endMonthSpinner: Spinner = view.findViewById(R.id.endMonthSpinner)
        val endYearSpinner: Spinner = view.findViewById(R.id.endYearSpinner)

        val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

        val current = Calendar.getInstance()
        val currentMonth = current.get(Calendar.MONTH)
        val currentYear = current.get(Calendar.YEAR)

        val years = (currentYear..(currentYear + 5)).map { it.toString() }

        startMonthSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)
        startYearSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)

        endMonthSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, months)
        endYearSpinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, years)

        startMonthSpinner.setSelection(0)
        startYearSpinner.setSelection(0)

        endMonthSpinner.setSelection(currentMonth)
        endYearSpinner.setSelection(0)

        // recycler history
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
        return inflater.inflate(R.layout.fragment_transaction_history, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TransactionHistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TransactionHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}