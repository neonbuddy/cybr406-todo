package com.cybr406.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.*;

import javax.net.ssl.HttpsURLConnection;
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

    @Autowired
    TaskJpaRepository taskJpaRepository;

    @Autowired
    TodoJpaRepository todoJpaRepository;

    @PostMapping("/todos")
    public ResponseEntity<Todo> createToDo(@Valid @RequestBody Todo todo) {

        Todo created = todoJpaRepository.save(todo);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/todos/{id}")
    public ResponseEntity<Todo> findTodo(@PathVariable long id){
        Optional<Todo> foundTodo = todoJpaRepository.findById(id);

        if(foundTodo.isPresent()){
            Todo test = foundTodo.get();

            return new ResponseEntity<>(test, HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/todos")
    public Page<Todo> findAll(Pageable page){
        return todoJpaRepository.findAll(page);
    }

    @PostMapping("/todos/{id}/tasks")
    public ResponseEntity<Todo> addTask(@PathVariable long id, @Valid @RequestBody Task task){
        Optional<Todo> optionalTodo = todoJpaRepository.findById(id);   //This is like an envelope holding a todo; gets rid of null pointers
        if(optionalTodo.isPresent()){
            Todo todo = optionalTodo.get();
            todo.getTasks().add(task);
            task.setTodo(todo);
            taskJpaRepository.save(task);
            return new ResponseEntity<>(todo, HttpStatus.CREATED);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/todos/{id}")
    public ResponseEntity deleteTodos(@PathVariable long id){
        //TODO Check if exists, then delete, or 404
        if(todoJpaRepository.existsById(id)){
            todoJpaRepository.deleteById(id);
            return  new ResponseEntity(HttpStatus.NO_CONTENT);
        }else{
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity deleteTaks(@PathVariable long id){
        if (taskJpaRepository.existsById(id)) {
            taskJpaRepository.deleteById(id);
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }else{
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
    }
}