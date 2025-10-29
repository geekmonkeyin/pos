package com.gkmonk.pos.controller.attendance;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/v1/attendance")
public class AttendanceController {

    @GetMapping
    public ModelAndView getAttendancePage() {
        ModelAndView modelAndView = new ModelAndView("attendance");
        return modelAndView;
    }
}
