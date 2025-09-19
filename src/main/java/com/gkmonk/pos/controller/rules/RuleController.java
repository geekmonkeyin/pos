package com.gkmonk.pos.controller.rules;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/v1/rules/")
public class RuleController {

    @GetMapping("")
    public ModelAndView rules() {
        ModelAndView modelAndView = new ModelAndView("rules");
        return modelAndView;
    }

}
