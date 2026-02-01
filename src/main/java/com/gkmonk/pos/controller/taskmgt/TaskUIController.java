package com.gkmonk.pos.controller.taskmgt;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TaskUIController {

    @RequestMapping(value = {
            "/",
            "/login",
            "/dashboard",
            "/tasks/**",
            "/weekly-planning",
            "/users/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
