package com.three19.todolist.model;

public class ToDo {
    private int id;
    private String name;
    private String dueDate;
    private String category;
    private String priority;
    private String status;

    // Constructor with all parameters
    public ToDo(int id, String name, String dueDate, String category, String priority, String status) {
        this.id = id;
        this.name = name;
        this.dueDate = dueDate;
        this.category = category;
        this.priority = priority;
        this.status = status;
    }

    // No-argument constructor
    public ToDo() {
        // Optionally set default values
        this.id = -1;
        this.name = "";
        this.dueDate = "";
        this.category = "";
        this.priority = "";
        this.status = "";
    }

    // Getter and Setter methods for all fields (if needed)

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
