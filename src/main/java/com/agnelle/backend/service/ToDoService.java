package com.agnelle.backend.service;

import com.agnelle.backend.entity.ToDo;
import com.agnelle.backend.repository.ToDoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ToDoService {

    @Autowired
    private ToDoRepository toDoRepository;

    public ToDo saveToDo(ToDo toDo) throws IOException {
        try {
            toDo.setDate(new Date());

            return toDoRepository.save(toDo);
        } catch (Exception e) {
            throw new IOException("Error saving to do item");
        }
    }

    public List<ToDo> getToDos() {
        return toDoRepository.findAll();
    }

    public ToDo getToDoById(Long id) {
        Optional<ToDo> toDo = toDoRepository.findById(id);

        if (toDo.isPresent()) {
            return toDo.get();
        } else {
            throw new RuntimeException("to do item not found");
        }
    }

    public ToDo deleteToDoById(Long id) {
        Optional<ToDo> toDo = toDoRepository.findById(id);

        if (toDo.isPresent()) {
            toDoRepository.delete(toDo.get());
            return toDo.get();
        } else {
            throw new RuntimeException("to do item not found or deleted.");
        }
    }

    public ToDo editToDoById(Long id, ToDo toDo) {
        Optional<ToDo> optionalToDo = toDoRepository.findById(id);

        if (optionalToDo.isPresent()) {
            ToDo newTodo = optionalToDo.get();

            if (toDo.getTitle() != null && !toDo.getTitle().equals(newTodo.getTitle())) {
                newTodo.setTitle(toDo.getTitle());
            }

            if (toDo.getChecked() != null && !toDo.getChecked().equals(newTodo.getChecked())) {
                newTodo.setChecked(toDo.getChecked());
            }

            newTodo.setDate(new Date());

            toDoRepository.save(newTodo);
            return toDo;
        } else {
            throw new RuntimeException("to do item not found or deleted.");
        }
    }
}
