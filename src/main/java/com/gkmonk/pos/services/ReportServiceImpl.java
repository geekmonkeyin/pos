package com.gkmonk.pos.services;

import com.gkmonk.pos.model.ReportDetails;
import com.gkmonk.pos.repo.ReportDetailsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl {

    @Autowired
    private ReportDetailsRepo reportDetailsRepo;

    public void saveReportDetails(List<ReportDetails> reportDetailsList) {

            reportDetailsRepo.saveAll(reportDetailsList);
    }

    public List<ReportDetails> getAllReportDetails() {
        return reportDetailsRepo.findAll();
    }

    public List<ReportDetails> getReportDetailsByDate(String startDate,String endDate) {
        return reportDetailsRepo.findByDate(startDate,endDate).orElse(null);
    }
}
