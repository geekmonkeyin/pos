package com.gkmonk.pos.controller.bookshopify;

import com.gkmonk.pos.model.order.PickItem;
import com.gkmonk.pos.services.orders.PicklistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/v1/order")
public class ShopifySummaryController {

    @Autowired
    private PicklistService service;


    @GetMapping("/picklist")
    public String picklist(@RequestParam(value = "q", required = false) String q, Model model) {
        List<PickItem> items = service.list(q);
        long picked = service.countPicked(items);
        model.addAttribute("items", items);
        model.addAttribute("picked",  picked);
        model.addAttribute("total", items.size());
        return "showsummary";
    }

    @PostMapping("/picklist/toggle")
    public String toggle(@RequestParam("id") String id,
                         @RequestParam(value = "q", required = false) String q) {
        service.toggle(id);
        return "redirect:/picklist" + (q != null && !q.isBlank() ? "?q=" + q : "");
    }

    @PostMapping("/picklist/markAll")
    public String markAll(@RequestParam(value = "ids", required = false) List<String> ids,
                          @RequestParam(value = "q", required = false) String q) {
        if (ids != null) service.markAllPicked(ids);
        return "redirect:/picklist" + (q != null && !q.isBlank() ? "?q=" + q : "");
    }
}
