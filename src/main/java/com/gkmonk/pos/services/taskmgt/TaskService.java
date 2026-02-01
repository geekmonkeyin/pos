package com.gkmonk.pos.services.taskmgt;

import com.gkmonk.pos.model.taskmgt.Attachment;
import com.gkmonk.pos.model.taskmgt.Consts;
import com.gkmonk.pos.model.taskmgt.Subtask;
import com.gkmonk.pos.model.taskmgt.Task;
import com.gkmonk.pos.model.taskmgt.TaskDtos;
import com.gkmonk.pos.model.taskmgt.User;
import com.gkmonk.pos.repo.taskmgt.TaskAttachmentRepo;
import com.gkmonk.pos.repo.taskmgt.TaskRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepo taskRepo;
    private final TaskAttachmentRepo taskAttachmentRepo; // ✅ NEW

    private String nowIso() {
        return DateTimeFormatter.ISO_INSTANT.format(Instant.now());
    }

    public List<Task> list(String status, String assignedTo) {
        if (status != null && assignedTo != null) return taskRepo.findByStatusAndAssignedToOrderByCreatedAtDesc(status, assignedTo);
        if (status != null) return taskRepo.findByStatusOrderByCreatedAtDesc(status);
        if (assignedTo != null) return taskRepo.findByAssignedToOrderByCreatedAtDesc(assignedTo);
        return taskRepo.findAllByOrderByCreatedAtDesc();
    }

    public Task create(TaskDtos.TaskCreateRequest req, User actor) {
        String id = "task-" + UUID.randomUUID();
        String now = nowIso();

        Task t = Task.builder()
                .id(id)
                .title(req.title)
                .description(req.description)
                .priorityId(req.priorityId)
                .teamTypeId(req.teamTypeId)
                .status(req.status != null ? req.status : Consts.TaskStatus.TODO)
                .assignedTo(req.assignedTo)
                .dueDate(req.dueDate)
                .createdBy(actor.getId())
                .createdAt(now)
                .updatedAt(now)
                .timeTaken(0)
                .build();

        return taskRepo.save(t);
    }

    public Task get(String taskId) {
        return taskRepo.findById(taskId).orElseThrow(() -> new IllegalArgumentException("Task not found"));
    }

    public Task update(String taskId, TaskDtos.TaskUpdateRequest req, User actor) {
        Task t = get(taskId);

        // member can only update tasks assigned to them (same as python) :contentReference[oaicite:3]{index=3}
        if (Consts.UserRole.MEMBER.equals(actor.getRole())) {
            if (t.getAssignedTo() == null || !t.getAssignedTo().equals(actor.getId())) {
                throw new SecurityException("You can only update tasks assigned to you");
            }
        }

        if (req.title != null) t.setTitle(req.title);
        if (req.description != null) t.setDescription(req.description);
        if (req.priorityId != null) t.setPriorityId(req.priorityId);
        if (req.teamTypeId != null) t.setTeamTypeId(req.teamTypeId);
        if (req.status != null) t.setStatus(req.status);
        if (req.assignedTo != null) t.setAssignedTo(req.assignedTo);
        if (req.dueDate != null) t.setDueDate(req.dueDate);
        if (req.timeTaken != null) t.setTimeTaken(req.timeTaken);

        t.setUpdatedAt(nowIso());
        return taskRepo.save(t);
    }

    public void delete(String taskId) {
        if (!taskRepo.existsById(taskId)) throw new IllegalArgumentException("Task not found");
        taskRepo.deleteById(taskId);
    }

    public Task addSubtask(String taskId, TaskDtos.SubtaskCreateRequest req) {
        Task t = get(taskId);
        t.getSubtasks().add(Subtask.builder()
                .id("subtask-" + UUID.randomUUID())
                .title(req.title)
                .completed(false)
                .build()
        );
        t.setUpdatedAt(nowIso());
        return taskRepo.save(t);
    }

    public Task updateSubtask(String taskId, String subtaskId, boolean completed) {
        Task t = get(taskId);
        boolean found = false;
        for (Subtask s : t.getSubtasks()) {
            if (s.getId().equals(subtaskId)) {
                s.setCompleted(completed);
                found = true;
                break;
            }
        }
        if (!found) throw new IllegalArgumentException("Task or subtask not found");
        t.setUpdatedAt(nowIso());
        return taskRepo.save(t);
    }

    public Task deleteSubtask(String taskId, String subtaskId) {
        Task t = get(taskId);
        t.getSubtasks().removeIf(s -> s.getId().equals(subtaskId));
        t.setUpdatedAt(nowIso());
        return taskRepo.save(t);
    }

    public Task addAttachment(String taskId, MultipartFile file, User actor) {
        Task t = get(taskId);

        try {
            byte[] bytes = file.getBytes();
            Attachment att = Attachment.builder()
                    .id("attachment-" + UUID.randomUUID())
                    .taskId(taskId) // ✅ reference to task
                    .filename(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .size(bytes.length)
                    .dataBase64(Base64.getEncoder().encodeToString(bytes))
                    .uploadedAt(nowIso())
                    .uploadedBy(actor.getUsername())
                    .build();

            Attachment attachment =  taskAttachmentRepo.save(att);
          //  t.getAttachments().add(attachment);
        } catch (Exception e) {
            throw new RuntimeException("File upload failed", e);
        }
        return t;
    }
}