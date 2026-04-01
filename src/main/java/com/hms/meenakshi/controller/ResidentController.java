package com.hms.meenakshi.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("resident")
public class ResidentController {


    @GetMapping("/dashboard")
    public String dashboard(Model model){
        model.addAttribute("mainContent", "resident-pages/dashboard");
        return "resident-pages/layout";
    }

    @GetMapping("/accommodation")
    public String accommodation(Model model){
        model.addAttribute("mainContent", "resident-pages/accommodation");
        return "resident-pages/layout";
    }

    @GetMapping("/my-payments")
    public String myPayments(Model model){
        model.addAttribute("mainContent", "resident-pages/my-payments");
        return "resident-pages/layout";
    }

    @GetMapping("/add-fee-receipt")
    public String addFeeReceipt(Model model){
        model.addAttribute("mainContent", "resident-pages/add-receipts");
        return "resident-pages/layout";
    }

    @GetMapping("/fee-receipts")
    public String myFeeReceipts(Model model){
        model.addAttribute("mainContent", "resident-pages/view-receipts");
        return "resident-pages/layout";
    }

    @GetMapping("/add-laundry")
    public String addLaundry(Model model){
        model.addAttribute("fullName", "Kalyan");
        model.addAttribute("mainContent", "resident-pages/add-laundry");
        return "resident-pages/layout";
    }

    @GetMapping("/contacts")
    public String contacts(Model model){
        model.addAttribute("mainContent", "resident-pages/contacts");
        return "resident-pages/layout";
    }
}
