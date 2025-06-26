package com.example.wealthforge.data

data class RewardSummary(
    val totalPoints: Int,
    val monthsWithBudgets: Int,
    val monthsWithSpending: Int
)

suspend fun calculateRewards(userId: Int, db: AppDatabase): RewardSummary {
    val budgetDao = db.budgetDao()
    val transactionsDao = db.transactionsDao()

    // Get distinct months for budgets and transactions
    val budgetMonths = budgetDao.getDistinctMonthYear(userId)
    val spendingMonths = transactionsDao.getDistinctMonthYear(userId)

    val monthsWithBudgets = budgetMonths.size
    val monthsWithSpending = spendingMonths.size

    val budgetPoints = monthsWithBudgets * 5
    val spendingPoints = monthsWithSpending * 5
    val bonusPoints = (monthsWithBudgets / 5) * 10

    val totalPoints = budgetPoints + spendingPoints + bonusPoints

    return RewardSummary(
        totalPoints = totalPoints,
        monthsWithBudgets = monthsWithBudgets,
        monthsWithSpending = monthsWithSpending
    )
}
