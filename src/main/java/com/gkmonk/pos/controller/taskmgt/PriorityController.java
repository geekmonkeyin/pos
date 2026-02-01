package com.gkmonk.pos.controller.taskmgt;

import com.gkmonk.pos.model.taskmgt.MiscDtos;
import com.gkmonk.pos.model.taskmgt.Priority;
import com.gkmonk.pos.repo.taskmgt.PriorityRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PriorityController {

    private final PriorityRepo priorityRepo;

    @GetMapping("/priorities")
    public List<Priority> getAll() {
        return priorityRepo.findAllByOrderByOrderAsc();
    }

    @PostMapping("/priorities")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Priority create(@Valid @RequestBody MiscDtos.PriorityCreateRequest req) {
        Priority p = Priority.builder()
                .id("priority-" + UUID.randomUUID())
                .name(req.name)
                .color(req.color)
                .order(req.order)
                .build();
        return priorityRepo.save(p);
    }

    @PutMapping("/priorities/{priorityId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Priority update(@PathVariable String priorityId, @Valid @RequestBody MiscDtos.PriorityCreateRequest req) {
        Priority p = priorityRepo.findById(priorityId).orElseThrow(() -> new IllegalArgumentException("Priority not found"));
        p.setName(req.name);
        p.setColor(req.color);
        p.setOrder(req.order);
        return priorityRepo.save(p);
    }

    @DeleteMapping("/priorities/{priorityId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Map<String, String> delete(@PathVariable String priorityId) {
        if (!priorityRepo.existsById(priorityId)) throw new IllegalArgumentException("Priority not found");
        priorityRepo.deleteById(priorityId);
        return Map.of("message", "Priority deleted successfully");
    }
}
