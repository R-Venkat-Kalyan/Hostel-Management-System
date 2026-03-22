package com.hms.meenakshi.controller;

import com.hms.meenakshi.entity.Room;
import com.hms.meenakshi.service.RoomService;
import lombok.AllArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@AllArgsConstructor
public class ManagerController {

    private final RoomService roomService;

    @GetMapping("/add-room")
    public String addRoom(Model model) {
        model.addAttribute("mainContent","/manager-pages/add-room");
        return "/manager-pages/layout";
    }

    @PostMapping("/save-room")
    public String saveRoom(@ModelAttribute Room room, Model model) {
        roomService.saveRoom(room);
        model.addAttribute("successMessage","Room Added Successfully");
        model.addAttribute("mainContent","/manager-pages/add-room");
        return "/manager-pages/layout";
    }
}
