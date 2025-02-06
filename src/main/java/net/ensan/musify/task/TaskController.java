package net.ensan.musify.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskRepository repository;

    @Autowired
    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public ResponseEntity<Long> createTask(@RequestBody TaskDto taskDto) {
        final Task task = new Task(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        final Task savedTask = repository.save(task);
        return new ResponseEntity<>(savedTask.getId(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        return repository.findById(id)
                .map(t -> new ResponseEntity<>(t.toDto(), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @PutMapping("/{id}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<String> updateTask(@PathVariable Long id, @RequestBody TaskDto taskDto) {
        return repository.findById(id)
                .map(t -> {
                    if (taskDto.getStatus() == null) {
                        return new ResponseEntity<>(
                                "Available statuses are: CREATED, APPROVED, REJECTED, BLOCKED, DONE.",
                                HttpStatus.BAD_REQUEST
                        );
                    }

                    TaskStatus taskStatus;
                    try {
                        taskStatus = TaskStatus.valueOf(taskDto.getStatus());
                    } catch (IllegalArgumentException e) {
                        return new ResponseEntity<>(
                                "Available statuses are: CREATED, APPROVED, REJECTED, BLOCKED, DONE.",
                                HttpStatus.BAD_REQUEST
                        );
                    }
                    if (taskDto.getTitle() != null) {
                        t.setTitle(taskDto.getTitle());
                    }
                    if (taskDto.getDescription() != null) {
                        t.setDescription(taskDto.getDescription());
                    }
                    t.setTaskStatus(taskStatus);
                    repository.save(t);
                    return new ResponseEntity(HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTask(@PathVariable Long id) {
        return repository.findById(id)
                .map(t -> {
                    repository.delete(t);
                    return new ResponseEntity(HttpStatus.OK);
                })
                .orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllResources() {
        Iterable<Task> iterable = repository.findAll();
        List<TaskDto> tasks = new ArrayList<>();
        iterable.forEach(task -> tasks.add(task.toDto()));
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

}
