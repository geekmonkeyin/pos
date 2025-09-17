package com.gkmonk.pos.controller.packaging;

// src/main/java/com/example/packaging/web/PackagingController.java

import com.gkmonk.pos.model.packaging.PackagingFilter;
import com.gkmonk.pos.model.packaging.PackagingRow;
import com.gkmonk.pos.model.packaging.PackagingSummary;
import com.gkmonk.pos.services.packaging.PackagingReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.PrintWriter;

@Controller
@RequestMapping("/v1/inbound")
public class PackagingController {

    @Autowired
    private PackagingReportService service;

    @GetMapping("/packagingreport")
    public String packaging(@ModelAttribute PackagingFilter filter, Model model) {
        PageImpl<PackagingRow> page = service.findRows(filter);
        PackagingSummary summary = service.buildSummary(filter);

        model.addAttribute("filter", filter);
        model.addAttribute("page", page);
        model.addAttribute("rows", page.getContent());
        model.addAttribute("summary", summary);
        model.addAttribute("labels", summary.getWeightBands().keySet());
        model.addAttribute("counts", summary.getWeightBands().values());

        return "packagingreport";
    }

    @GetMapping("/packaging/export")
    public void export(@ModelAttribute PackagingFilter filter, HttpServletResponse resp) throws Exception {
        PageImpl<PackagingRow> page = service.findRows(filter); // export current page (or load all if you prefer)
        resp.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=packaging_report.csv");
        resp.setContentType(MediaType.TEXT_PLAIN_VALUE);

        try (PrintWriter w = resp.getWriter()) {
            w.println("OrderNo,Length(cm),Width(cm),Height(cm),ActualKg,VolumetricKg,ChargeableKg,Courier,Warehouse,Status,Box");
            for (PackagingRow r : page.getContent()) {
                w.printf("%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%s,%s,%s,%s%n",
                        nv(r.getOrderNo()), nz(r.getLength()), nz(r.getWidth()), nz(r.getHeight()),
                        nz(r.getActualWeightKg()), nz(r.getVolumetricWeightKg()), nz(r.getChargeableWeightKg()),
                        nv(r.getCourier()), nv(r.getWarehouse()), nv(r.getStatus()), nv(r.getRecommendedBoxCode()));
            }
        }
    }

    private static String nv(String s) { return s == null ? "" : s.replace(",", " "); }
    private static double nz(Double d) { return d == null ? 0.0 : d; }
}

