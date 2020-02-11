package com.cybr406.todo;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Todo {

    private Long id;

    @NotBlank(message = "Author cannot be empty")
    private String author;

    @NotBlank(message = "Author cannot be empty")
    private String details;

    private List<Task> tasks = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return Objects.equals(id, todo.id) &&
                Objects.equals(author, todo.author) &&
                Objects.equals(details, todo.details) &&
                Objects.equals(tasks, todo.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, details, tasks);
    }
}
