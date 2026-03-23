package com.hms.meenakshi.controller;

import com.hms.meenakshi.entity.Room;
import com.hms.meenakshi.entity.User;
import com.hms.meenakshi.service.RoomService;
import com.hms.meenakshi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@AllArgsConstructor
@Controller
public class ManagerController {

    private final RoomService roomService;
    private final UserService userService;

    @GetMapping("/add-room")
    public String addRoom(Model model) {
        model.addAttribute("mainContent","manager-pages/add-room");
        return "/manager-pages/layout";
    }

    @PostMapping("/save-room")
    public String saveRoom(@ModelAttribute Room room, Model model) {
        roomService.saveRoom(room);
        model.addAttribute("successMessage","Room Added Successfully");
        model.addAttribute("mainContent","/manager-pages/add-room");
        return "/manager-pages/layout";
    }

    @GetMapping("/add-resident")
    public String addResident(Model model) {
        model.addAttribute("mainContent","/manager-pages/add-resident");
        return "/manager-pages/layout";
    }

    @PostMapping("/save-resident")
    public String saveResident(@ModelAttribute User user, Model model) {
        user.setRoomId("NEW");
        user.setFeeSummaryId("NEW");
        userService.saveUser(user);
        model.addAttribute("successMessage","User Added Successfully");
        model.addAttribute("mainContent","/manager-pages/add-resident");
        return "/manager-pages/layout";
    }

    @GetMapping("/assign-room")
    public String showAssignPage(Model model) {
        // 1. Get residents where roomId == "NEW"
        List<User> unassigned = userService.findByRoleAndRoomId("RESIDENT", "NEW");

        // 2. Get rooms where occupiedCount < capacity
        List<Room> available = roomService.getAvailableRoooms();

        model.addAttribute("unassignedResidents", unassigned);
        model.addAttribute("availableRooms", available);
        model.addAttribute("mainContent", "manager-pages/assign-room");

        return "manager-pages/layout";
    }



}
