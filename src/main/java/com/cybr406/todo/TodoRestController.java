package com.cybr406.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;


@RestController
public class TodoRestController {

    @Autowired
    InMemoryTodoRepository repo;

    @PostMapping("/todos")
    public ResponseEntity<Todo> createToDo(@Valid @RequestBody Todo todo) {

        if (todo.getAuthor().isEmpty() || todo.getDetails().isEmpty()) {
            return new ResponseEntity<>(todo, HttpStatus.BAD_REQUEST);
        }else {
            return new ResponseEntity<>(repo.create(todo), HttpStatus.CREATED);
        }
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> findTodo(@PathVariable long id){

        Optional<Todo> list = repo.find(id);

        if(list.isPresent()){
            Todo test = list.get();

            return new ResponseEntity<>(test, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/todos")
    public ResponseEntity<List<Todo>> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        List<Todo> list = repo.findAll(page, size);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/todos/{id}/tasks")
    public ResponseEntity<Todo> addTask(@PathVariable long id, @Valid @RequestBody Task task){
        Todo todo = repo.addTask(id, task);
        return new ResponseEntity<>(todo, HttpStatus.CREATED);
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity deleteTodos(@PathVariable long id){
        try{
            repo.delete(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch(NoSuchElementException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity deleteTaks(@PathVariable long id){
        try{
            repo.deleteTask(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } catch(NoSuchElementException e) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}
