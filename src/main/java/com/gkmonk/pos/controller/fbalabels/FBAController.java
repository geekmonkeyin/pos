package com.gkmonk.pos.controller.fbalabels;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/v1/fba")
public class FBAController {

    @GetMapping("/generatelabel")
    public ModelAndView generateFBALabel() {
        return new ModelAndView("amazon-fba-labels");
    }
}
