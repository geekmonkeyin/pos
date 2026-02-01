package com.gkmonk.pos.controller.taskmgt;

import com.gkmonk.pos.model.taskmgt.Task;
import com.gkmonk.pos.model.taskmgt.TaskPriority;
import com.gkmonk.pos.model.taskmgt.TaskStatus;
import com.gkmonk.pos.services.employee.EmployeeDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/v1/taskmgt")
public class TaskMgtController {

    @Autowired
    private EmployeeDetailsService employeeDetailsService;

    @RequestMapping("/dashboard")
    public ModelAndView viewDashboard() {
        return new ModelAndView("taskmgt/ai-dashboard");
    }

    @RequestMapping("")
    public ModelAndView taskmgt() {
        return new ModelAndView("taskmgt/taskmgt");
    }

    @RequestMapping("/tasks/new")
    public ModelAndView newTask() {
        ModelAndView model =  new ModelAndView("taskmgt/add-task");

        Task task = new Task();
        //task.setTaskId(generateTaskId());
        //task.setStatus(TaskStatus.TODO);

        model.addObject("task", task);
        model.addObject("users", employeeDetailsService.getAllUsers());
        model.addObject("priorities", TaskPriority.values());
        model.addObject("statuses", TaskStatus.values());
        return model;
    }


    private String generateTaskId() {
        return "GM-" + (1000 + (int)(Math.random() * 9000));
    }

}
