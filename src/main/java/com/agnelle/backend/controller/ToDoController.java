package com.agnelle.backend.controller;
import com.agnelle.backend.entity.ToDo;
import com.agnelle.backend.service.ToDoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/todos")
@CrossOrigin(origins = "*")
public class ToDoController {

    @Autowired
    private ToDoService toDoService;

    @PostMapping
    public ResponseEntity<?> createToDo(@RequestBody ToDo toDo) {
        try {
            ToDo createdToDo = toDoService.saveToDo(toDo);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdToDo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ToDo>> getAllToDos() {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(toDoService.getToDos());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getToDo(@PathVariable Long id) {
        try {
            ToDo toDo = toDoService.getToDoById(id);

            if (toDo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            return ResponseEntity.ok(toDo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteToDo(@PathVariable Long id) {
        try {
            ToDo toDo = toDoService.deleteToDoById(id);
            return ResponseEntity.ok("To do item deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/edit/{id}")
    public ResponseEntity<?> editToDoById(@PathVariable Long id, @RequestBody ToDo toDo) {
        try {
            ToDo toDoToEdit = toDoService.editToDoById(id, toDo);
            return ResponseEntity.ok(toDoToEdit);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
