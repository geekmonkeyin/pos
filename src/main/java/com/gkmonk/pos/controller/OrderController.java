package com.gkmonk.pos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/v1/order")
public class OrderController {

    @RequestMapping("create")
    public ModelAndView createOrder() {
        ModelAndView model = new ModelAndView();
        model.setViewName("neworder");
        return model;
    }

    @RequestMapping("update")
    public String updateOrder() {
        return "order/update";
    }

    @RequestMapping("delete")
    public String deleteOrder() {
        return "order/delete";
    }

    @RequestMapping("")
    public ModelAndView viewOrder() {
        ModelAndView model = new ModelAndView();
        model.setViewName("orders");
        return model;
    }
    @RequestMapping("/replacement")
    public ModelAndView replaceOrder() {
        ModelAndView model = new ModelAndView();
        model.setViewName("replacement");
        return model;
    }

    @RequestMapping("/bookshopify")
    public ModelAndView bookShopify() {
        ModelAndView model = new ModelAndView();
        model.setViewName("bookshopify");
        return model;
    }
}
