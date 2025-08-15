package com.gkmonk.pos.controller;

import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.pod.services.PODServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("/v1/order/packorder")
public class PackOrderController {

    @Autowired
    private PODServiceImpl podService;

    @GetMapping("")
    public ModelAndView packOrder(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("packorder");
        return modelAndView;
    }

    @GetMapping("/getOrderByAwb/{awb}")
    public ResponseEntity<PackedOrder> getOrderByAwb(@PathVariable("awb") String awb) {

        return ResponseEntity.ok(new PackedOrder());
    }
}
