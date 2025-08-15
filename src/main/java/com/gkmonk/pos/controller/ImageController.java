package com.gkmonk.pos.controller;

import com.gkmonk.pos.services.ImageDBServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/image")
public class ImageController {

    @Autowired
    private ImageDBServiceImpl imageDBService;

    @GetMapping("/{imageId}")
    public ResponseEntity<List<byte[]>> fetchImage(@PathVariable String imageId) {
        List<byte[]> image = imageDBService.fetchInventoryImagesById(imageId);
        return ResponseEntity.ok().body(image);
    }

}
