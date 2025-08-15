package com.gkmonk.pos.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class DBSyncController {

    //@Autowired
   // private MongoSyncService mongoSyncService;

    //@Scheduled(fixedDelay = 60000) // runs every 60 seconds
    public void syncDatabase() {
        // Logic to sync database goes here
        System.out.println("Database sync operation executed.");
      //  mongoSyncService.syncData();
    }
}
