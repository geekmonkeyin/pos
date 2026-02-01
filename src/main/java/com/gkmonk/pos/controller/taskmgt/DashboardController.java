package com.gkmonk.pos.controller.taskmgt;

import com.gkmonk.pos.model.taskmgt.MiscDtos;
import com.gkmonk.pos.services.taskmgt.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/performance")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public List<MiscDtos.PerformanceMetric> performance() {
        return dashboardService.performance();
    }
}
