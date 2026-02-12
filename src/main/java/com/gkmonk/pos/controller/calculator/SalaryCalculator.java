package com.gkmonk.pos.controller.calculator;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/v1/calculator/")
public class SalaryCalculator {

    @GetMapping("salary")
    public ModelAndView calculateSalary() {
        // Placeholder logic for salary calculation
        return new ModelAndView("SalaryCalculator");
    }

}
