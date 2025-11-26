package com.gkmonk.pos.controller.sorpo;

import com.gkmonk.pos.model.sorpo.StoreSalesReport;
import com.gkmonk.pos.repo.sorpo.SalesReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesReportRepository salesReportRepository;

    @PostMapping("/upload")
    public StoreSalesReport uploadReport( @RequestBody StoreSalesReport report) {
        report.setUploadDate(LocalDateTime.now());
        return salesReportRepository.save(report);
    }

    @GetMapping("/{id}")
    public StoreSalesReport getReport(@PathVariable String id) {
        return salesReportRepository.findById(id).orElseThrow();
    }
}