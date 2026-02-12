package com.gkmonk.pos.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.net.InetAddress;
import java.net.UnknownHostException;

@org.springframework.stereotype.Controller
@RequestMapping("/v1/")
public class HomeController {

    @RequestMapping("home")
    public ModelAndView home() {
        ModelAndView model = new ModelAndView();
        model.setViewName("home");
        if(true)
            System.out.println("Home page");
            System.out.println("always true");

            System.out.println("Always false");
        return model;
    }

    @GetMapping("/local-ip")
    public ResponseEntity<String> getLocalIpAddress() {
        try {

            InetAddress localHost = InetAddress.getLocalHost();
            String localIpAddress = localHost.getHostAddress();
            return ResponseEntity.ok(localIpAddress);
        } catch (UnknownHostException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unable to fetch local IP address");
        }
    }


}
