# todoListApp

# 📋 ToDo List Mobile Application

This project is a mobile ToDo List app built with Java, Android Studio, and SQLite. It allows users to create, update, delete, filter, and view tasks based on categories, priorities, and due dates.

## 📱 Features

- Add, update, and delete tasks
- Set task name, due date, category, priority, and status
- Filter tasks by category and priority
- Progress bar showing task completion status
- View all tasks in a separate activity
- Clear inputs easily
- Display task counts by priority
- Input validation to ensure all task fields are properly filled
- Real-time user feedback with Toast and Snackbar messages
- Built-in Dark Mode support for better user comfort
- Push notifications for task-related feedback

---

## 🚀 Enhancements Implemented

### ✅ User Experience Enhancements
- **Input Validation:** Prevents saving tasks with empty fields. Users receive instant feedback via Toast messages.
- **Snackbar Notifications:** Improved user interaction by providing success/error messages when adding or updating tasks.
- **Dark Mode Support:** App automatically switches between light and dark themes based on device settings.

### ✅ Algorithms and Data Structures Enhancements
- **AI Task Prioritization:** Tasks are automatically sorted based on status, priority, and due date using optimized SQL queries and in-memory sorting.
- **Future AI Expansion Ready:** Architecture supports building more advanced machine learning models (e.g., decision trees, k-NN) for task prioritization.
  
### ✅ Database Enhancements
- **Database Indexing:** Created indexes on `due_date` and `priority` fields for faster query performance.
- **Advanced SQL Filtering:** Implemented methods to filter tasks by priority and status.
- **Task Aggregation:** Added functionality to display task counts grouped by priority.

---

## 🛠 Technologies Used
- Java
- Android Studio
- SQLite Database
- Material Design Components (Dark Mode, Snackbar, Progress Bar)

---

## 📚 Installation

1. Clone the repository:

```bash
git clone https://github.com/AnnamarieCortes/todoListApp.git
