# WealthForge

WealthForge is a comprehensive financial management app designed to help users manage their personal finances, track expenses, and achieve financial goals. With WealthForge, users can set budgets, create and track expense entries, upload receipts, and gain insights into their spending.

## Features

* **User Authentication:** Users can log in securely using their username and password, ensuring personalized financial management.

* **Create Expense and Goal Categories:** Users can create custom expense categories and financial goals to organize their spending and savings targets.

* **Expense Entry:** Users can create expense entries specifying the year, month, description, category, and upload receipts for better documentation of their financial activity.

* **Set Budget:** Users can set their monthly or yearly budget for specific categories, helping them manage their finances effectively.

* **Expense History:** View a comprehensive history of all expenses and transactions, providing insights into past spending patterns.

* **Spending Total by Category:** Users can see the total spending within each category over a given date range, helping them track progress towards their financial goals.

## How to Use

### 1. **User Authentication**

Login to the app using your username and password to gain access to your personalized dashboard where you can manage your finances.

### 2. **Create Expense and Goal Categories**

Create custom expense categories to categorize your spending. You can also set financial goals to track your progress.

### 3. **Add Expense Entry**

Create expense entries by specifying the year, month, category, and description. You can also upload a receipt for each transaction for future reference.

### 4. **Set Your Budget**

Set a budget for different categories and track your spending to ensure that you stay within your financial limits.

### 5. **View Expense History**

Review your entire transaction history and monitor how your spending evolves over time. This feature provides an overview of all your recorded expenses.

### 6. **View Spending by Category**

Use the date filter to view total spending by category within a specific date range. This feature provides insights into where your money is being spent and helps you make better budgeting decisions.

## Installation

Clone the repository to your local machine:

```bash
git clone https://github.com/your-username/wealthforge.git
```

Navigate into the project directory:

```bash
cd wealthforge
```

Install the necessary dependencies:

```bash
./gradlew build
```

Run the app on your Android device or emulator.

# Final POE 

## Implementation of Lecturer's Feedback

Linked all activities/fragments to the database

## Updated Features

### Budget vs. Actual Spending Charts
- Budget vs. Actual Spending (Per Category): Displays how much was budgeted vs. spent for each category over a selected date range.
- Budget vs. Actual Spending (Per Month): Visualizes overall monthly budgets compared to actual expenses.

### Fixed Budget Calculation Issue
- Budget values now correctly reflect the intended amount, even if multiple transactions exist for the same category and month.
- Prevents incorrect duplication of budget totals.

## New Features

### Expense Summary Improvements
- The budget summary now clearly explains whether the user saved, overspent, or matched their budget.
- Highlights the highest overspending category with its overspend percentage.
- All currency values are formatted with thousands separators for better readability (e.g. `R1 234.56`).

### Password Reset Functionality
- Users who forget their password can reset it by providing their username and choosing a new password.

### Tips Popup on Sign-In:**  
  After signing in, users are greeted with a helpful popup that provides financial tips and app usage guidance to promote better money management habits.

### Transaction Details Dialog:**  
  Each transaction can now be viewed in detail, including the full date, category, description, amount, and uploaded receipt image.

### Total Spending Display:**  
  Total spending amounts are now shown at the bottom of the **Transaction History** and **Spending by Category** pages, giving users quick insight into their overall expenses.
  
## Contributors

ST10355256 Halalisile Mzobe 
ST10341842 Buhlebenkosi Mvinjelwa 
ST10363752 Asanda Ngiba 
ST10396910 Nhlonipho Fakazi 
ST10395082 Ziphozonke Nxumalo 

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Demo Video

The demo was screen recorded from an android phone.

https://youtu.be/0b8GmuzJoW0
