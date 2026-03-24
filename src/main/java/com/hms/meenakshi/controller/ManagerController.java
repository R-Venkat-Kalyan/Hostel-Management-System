package com.hms.meenakshi.controller;

import com.hms.meenakshi.dto.ResidentDetailsDTO;
import com.hms.meenakshi.entity.Room;
import com.hms.meenakshi.entity.User;
import com.hms.meenakshi.service.RoomAssignmentService;
import com.hms.meenakshi.service.RoomService;
import com.hms.meenakshi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final RoomService roomService;
    private final UserService userService;
    private final RoomAssignmentService assignmentService;

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
        List<Room> available = roomService.getAvailableRooms();

        model.addAttribute("unassignedResidents", unassigned);
        model.addAttribute("availableRooms", available);
        model.addAttribute("mainContent", "manager-pages/assign-room");

        return "manager-pages/layout";
    }

    @PostMapping("/create-assignment")
    public String processAssignment(@RequestParam String userId,
                                    @RequestParam String roomId,
                                    RedirectAttributes redirectAttributes) {
        try {
            assignmentService.assignRoomToResident(userId, roomId);
            redirectAttributes.addFlashAttribute("successMessage", "Resident assigned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("successMessage", "Error: " + e.getMessage());
        }
        return "redirect:/assign-room";
    }

    @GetMapping("/view-residents")
    public String viewResidents(Model model) {
        List<ResidentDetailsDTO> residentList = userService.getAllResidentSnapshots();
        model.addAttribute("residents", residentList);
        // Standardizing the total calculation for the footer
        double totalDue = residentList.stream().mapToDouble(ResidentDetailsDTO::getDuePayment).sum();
        model.addAttribute("totalDue", totalDue);
        model.addAttribute("mainContent", "manager-pages/view-residents");
        return "manager-pages/layout";
    }


}
