package com.gkmonk.pos.controller.taskmgt;

import com.gkmonk.pos.model.taskmgt.Task;
import com.gkmonk.pos.services.taskmgt.WeeklyPlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WeeklyPlanningController {

    private final WeeklyPlanningService weeklyPlanningService;

    @GetMapping("/weekly-planning")
    public Map<String, List<Task>> weekly() {
        return weeklyPlanningService.weekly();
    }
}