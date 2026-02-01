package com.gkmonk.pos.controller.taskmgt;


import com.gkmonk.pos.configuration.security.CurrentUser;
import com.gkmonk.pos.configuration.security.UserPrincipal;
import com.gkmonk.pos.model.taskmgt.Task;
import com.gkmonk.pos.model.taskmgt.TaskDtos;
import com.gkmonk.pos.services.taskmgt.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/tasks")
    public List<Task> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String assigned_to
    ) {
        return taskService.list(status, assigned_to);
    }

    @PostMapping("/tasks")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public Task create(@Valid @RequestBody TaskDtos.TaskCreateRequest req, @CurrentUser UserPrincipal principal) {
        return taskService.create(req, principal.getUser());
    }

    @GetMapping("/tasks/{taskId}")
    public Task get(@PathVariable String taskId) {
        return taskService.get(taskId);
    }

    @PutMapping("/tasks/{taskId}")
    public ResponseEntity<?> update(@PathVariable String taskId, @RequestBody TaskDtos.TaskUpdateRequest req, @CurrentUser UserPrincipal principal) {
        try {
            return ResponseEntity.ok(taskService.update(taskId, req, principal.getUser()));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of("detail", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("detail", e.getMessage()));
        }
    }

    @DeleteMapping("/tasks/{taskId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<?> delete(@PathVariable String taskId) {
        try {
            taskService.delete(taskId);
            return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("detail", e.getMessage()));
        }
    }

    // Subtasks
    @PostMapping("/tasks/{taskId}/subtasks")
    public Task addSubtask(@PathVariable String taskId, @Valid @RequestBody TaskDtos.SubtaskCreateRequest req) {
        return taskService.addSubtask(taskId, req);
    }

    @PutMapping("/tasks/{taskId}/subtasks/{subtaskId}")
    public Task updateSubtask(@PathVariable String taskId, @PathVariable String subtaskId, @RequestBody TaskDtos.SubtaskUpdateRequest req) {
        return taskService.updateSubtask(taskId, subtaskId, req.completed);
    }

    @DeleteMapping("/tasks/{taskId}/subtasks/{subtaskId}")
    public Task deleteSubtask(@PathVariable String taskId, @PathVariable String subtaskId) {
        return taskService.deleteSubtask(taskId, subtaskId);
    }

    // Attachments
    @PostMapping(value = "/tasks/{taskId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Task uploadAttachment(@PathVariable String taskId,
                                 @RequestPart("file") MultipartFile file,
                                 @CurrentUser UserPrincipal principal) {
        return taskService.addAttachment(taskId, file, principal.getUser());
    }
}