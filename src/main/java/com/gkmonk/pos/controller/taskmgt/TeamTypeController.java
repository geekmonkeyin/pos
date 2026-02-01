package com.gkmonk.pos.controller.taskmgt;

import com.gkmonk.pos.model.taskmgt.MiscDtos;
import com.gkmonk.pos.model.taskmgt.TeamType;
import com.gkmonk.pos.repo.taskmgt.TeamTypeRepo;
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
public class TeamTypeController {

    private final TeamTypeRepo teamTypeRepo;

    @GetMapping("/team-types")
    public List<TeamType> getAll() {
        return teamTypeRepo.findAll();
    }

    @PostMapping("/team-types")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public TeamType create(@Valid @RequestBody MiscDtos.TeamTypeCreateRequest req) {
        TeamType t = TeamType.builder()
                .id("team-" + UUID.randomUUID())
                .name(req.name)
                .color(req.color)
                .build();
        return teamTypeRepo.save(t);
    }

    @PutMapping("/team-types/{teamId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public TeamType update(@PathVariable String teamId, @Valid @RequestBody MiscDtos.TeamTypeCreateRequest req) {
        TeamType t = teamTypeRepo.findById(teamId).orElseThrow(() -> new IllegalArgumentException("Team type not found"));
        t.setName(req.name);
        t.setColor(req.color);
        return teamTypeRepo.save(t);
    }

    @DeleteMapping("/team-types/{teamId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Map<String, String> delete(@PathVariable String teamId) {
        if (!teamTypeRepo.existsById(teamId)) throw new IllegalArgumentException("Team type not found");
        teamTypeRepo.deleteById(teamId);
        return Map.of("message", "Team type deleted successfully");
    }
}
