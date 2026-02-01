package com.gkmonk.pos.controller.order;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/v1/order/abandoncart")
public class AbandoncartController {


    @RequestMapping("")
    public ModelAndView viewAbandoncarts() {
        return new ModelAndView("abandoncart");
    }

}
