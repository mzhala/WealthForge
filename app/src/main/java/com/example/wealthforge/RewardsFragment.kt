package com.example.wealthforge

import UserViewModel
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.wealthforge.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RewardsFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rewards, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.updateToolbarTitle("Rewards")

        // ✅ Initialize the database
        db = AppDatabase.getDatabase(requireContext())

        // Optional: if you want to use userViewModel here too
        userViewModel = (activity as MainActivity).userViewModel

        val pointsText = view.findViewById<TextView>(R.id.pointsTextView)
        val levelText = view.findViewById<TextView>(R.id.levelTextView)

        lifecycleScope.launch {
            val userId = userViewModel.userId.value?.toIntOrNull()
            if (userId != null) {
                val summary = withContext(Dispatchers.IO) {
                    calculateRewards(userId, db)
                }

                pointsText.text = "⭐ Total Points: ${summary.totalPoints}"
                levelText.text = "🏅 Level: " + when {
                    summary.totalPoints >= 100 -> "Gold"
                    summary.totalPoints >= 40 -> "Silver"
                    summary.totalPoints >= 20 -> "Bronze"
                    else -> "Rookie"
                }
            }
        }
    }
}
