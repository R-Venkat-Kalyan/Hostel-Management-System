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
