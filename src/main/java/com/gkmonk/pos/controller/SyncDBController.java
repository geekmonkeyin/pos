package com.gkmonk.pos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/v1/syncdb")
public class SyncDBController {

    @GetMapping("")
    public void syncDB(@RequestParam String targetUrl){

        //sync


    }

}

