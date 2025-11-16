package com.gkmonk.pos.controller.outward;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/v1/outward")
public class OutwardController {

    @GetMapping("")
    public ModelAndView getOutwardPage() {
        ModelAndView modelAndView = new ModelAndView("outwards");
        return modelAndView;
    }

}
