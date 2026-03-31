package com.hms.meenakshi.controller;

import com.hms.meenakshi.dto.PaymentHistoryDTO;
import com.hms.meenakshi.dto.ResidentDetailsDTO;
import com.hms.meenakshi.entity.Room;
import com.hms.meenakshi.entity.User;
import com.hms.meenakshi.service.PaymentService;
import com.hms.meenakshi.service.RoomAssignmentService;
import com.hms.meenakshi.service.RoomService;
import com.hms.meenakshi.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/manager")
public class ManagerController {

    private final RoomService roomService;
    private final UserService userService;
    private final RoomAssignmentService assignmentService;
    private final PaymentService paymentService;

    @GetMapping("/dashboard")
    public String dashBoard(RedirectAttributes redirectAttributes, Model model){
        model.addAttribute("mainContent", "manager-pages/dashboard");
        return "manager-pages/layout";
    }

    @GetMapping("/add-room")
    public String addRoom(Model model) {
        model.addAttribute("mainContent", "manager-pages/add-room");
        return "manager-pages/layout";
    }

    @PostMapping("/save-room")
    public String saveRoom(@ModelAttribute Room room, Model model,RedirectAttributes redirectAttributes) {
        roomService.saveRoom(room);
        redirectAttributes.addFlashAttribute("successMessage", "Room Added Successfully");
        return "redirect:/manager/view-rooms";

    }

    @GetMapping("/add-resident")
    public String addResident(Model model) {
        model.addAttribute("mainContent", "manager-pages/add-resident");
        return "manager-pages/layout";
    }

    @PostMapping("/save-resident")
    public String saveResident(@ModelAttribute User user, Model model, RedirectAttributes redirectAttributes) {
        user.setRoomId("NEW");
        user.setFeeSummaryId("NEW");
        userService.saveUser(user);
        redirectAttributes.addFlashAttribute("successMessage", "Resident Added Successfully");
        return "redirect:/manager/view-residents";
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

    @GetMapping("/collect-payment/{userId}")
    public String showPaymentForm(@PathVariable String userId, Model model, RedirectAttributes redirectAttributes) {
        try {
            ResidentDetailsDTO resident = userService.getSingleResidentSnapshot(userId);
            if (resident == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Resident not found!");
                return "redirect:/manager/view-residents";
            }
            model.addAttribute("resident", resident);
            model.addAttribute("mainContent", "manager-pages/collect-payment");
            return "manager-pages/layout";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/manager/view-residents";
        }
    }

    @PostMapping("/save-payment")
    public String savePayment(@RequestParam String userId,
                              @RequestParam double amount,
                              @RequestParam String paymentMode,
                              @RequestParam(required = false) String transactionRef,
                              RedirectAttributes redirectAttributes) {
        String managerId = "Bannu";
        try {
            paymentService.recordPendingPayment(userId, amount, paymentMode, transactionRef, managerId);
            redirectAttributes.addFlashAttribute("successMessage", "Payment Record Submitted for Approval!");
            return "redirect:/manager/view-payments";
        } catch (Exception e) {
            // Log the error to console so you can see it
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("successMessage", "Failed to save: " + e.getMessage());
            return "redirect:/manager/collect-payment/" + userId;
        }
    }

    @GetMapping("/view-payments")
    public String viewPayments(@RequestParam(required = false) String month, Model model) {
        if (month == null) month = YearMonth.now().toString();

        List<PaymentHistoryDTO> history = paymentService.getFilteredPayments(month);

        model.addAttribute("payments", history);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("mainContent", "manager-pages/view-payments");
        return "manager-pages/layout";
    }

    @GetMapping("/view-rooms")
    public String viewRooms(Model model) {
        List<Room> rooms = roomService.getAllRooms();

        // Sort numerically by room number (e.g., 101, 102, 201)
        rooms.sort(Comparator.comparing(Room::getRoomNumber));

        // Calculate totals for the footer
        int totalOccupied = rooms.stream().mapToInt(Room::getOccupiedCount).sum();
        int totalCapacity = rooms.stream().mapToInt(Room::getCapacity).sum();
        int totalVacant = totalCapacity - totalOccupied;

        model.addAttribute("rooms", rooms);
        model.addAttribute("totalOccupied", totalOccupied);
        model.addAttribute("totalVacant", totalVacant);
        model.addAttribute("mainContent", "manager-pages/view-rooms");

        return "manager-pages/layout";
    }

}
