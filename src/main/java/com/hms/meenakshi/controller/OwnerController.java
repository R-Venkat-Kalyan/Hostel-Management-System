package com.hms.meenakshi.controller;

import com.hms.meenakshi.dto.Expenses;
import com.hms.meenakshi.dto.PaymentApprovalDTO;
import com.hms.meenakshi.dto.PaymentHistoryDTO;
import com.hms.meenakshi.dto.ResidentDetailsDTO;
import com.hms.meenakshi.entity.Room;
import com.hms.meenakshi.entity.User;
import com.hms.meenakshi.service.ExpensesService;
import com.hms.meenakshi.service.PaymentService;
import com.hms.meenakshi.service.RoomService;
import com.hms.meenakshi.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.YearMonth;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/owner")
public class OwnerController {

    private final PaymentService paymentService;
    private final UserService userService;
    private final ExpensesService expensesService;
    private final RoomService roomService;

    @GetMapping("/dashboard")
    public String dashBoard(Model model, HttpSession session) {
//        User user = (User) session.getAttribute("user");
//        Boolean isVerified = (Boolean) session.getAttribute("isVerified");
//        if (user == null || isVerified == null || !isVerified) {
//            return "redirect:/auth/security-challenge";
//        }
        // 1. Residing Students
        List<User> residents = userService.findByRole("RESIDENT"); // Adjust null if your logic uses "NEW" or a specific RoomId check
        long totalStudents = residents.size();

        // 2. Room & Bed Logic (Reuse your viewRooms logic)
        List<Room> rooms = roomService.getAllRooms();
        // int totalOccupied = rooms.stream().mapToInt(Room::getOccupiedCount).sum();
        // int totalCapacity = rooms.stream().mapToInt(Room::getCapacity).sum();

        int availableRooms = (int) rooms.stream().filter(r -> r.getOccupiedCount() < r.getCapacity()).count();
//        int availableBeds = totalCapacity - totalOccupied;

        // 3. Pending Approvals (Using PaymentService)
        // Assuming you have a way to filter by 'PENDING' status in your history or a count method
        String currentMonth = YearMonth.now().toString();
        long pendingApprovals = paymentService.getFilteredPayments(currentMonth).stream()
                .filter(p -> "PENDING".equalsIgnoreCase(p.getStatus()))
                .count();

        // 4. Financials
        List<PaymentHistoryDTO> currentMonthPayments = paymentService.getFilteredPayments(currentMonth);
        double collectedThisMonth = currentMonthPayments.stream()
                .filter(p -> "APPROVED".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(PaymentHistoryDTO::getAmount)
                .sum();

        // For Outstanding, we use your Resident Snapshot logic
        List<ResidentDetailsDTO> residentList = userService.getAllResidentSnapshots();
        double totalOutstanding = residentList.stream().mapToDouble(ResidentDetailsDTO::getDuePayment).sum();

        // Adding to Model
        model.addAttribute("totalStudents", totalStudents);
        model.addAttribute("availableRooms", availableRooms);
//        model.addAttribute("availableBeds", availableBeds);
        model.addAttribute("pendingApprovals", pendingApprovals);

        model.addAttribute("last_date", collectedThisMonth); // Match HTML variable
        model.addAttribute("deadline", totalOutstanding);   // Match HTML variable

        model.addAttribute("mainContent", "owner-pages/dashboard");
        return "owner-pages/layout";
    }

//    @GetMapping("/add-manager")
//    public String addManager(){
//        return "owner-pages/add-manager";
//    }
//
//    @PostMapping("/owner/save-manager")
//    public String saveManager(@ModelAttribute User user){
//        user.setFeeSummaryId("NA");
//        user.setRoomId("NA");
//        userService.saveUser(user);
//        return "redirect:/add-manager";
//    }

    @GetMapping("/add-staff")
    public String addStaff(Model model) {
        model.addAttribute("mainContent", "owner-pages/add-staff");
        return "owner-pages/layout";
    }

    @PostMapping("/save-staff")
    public String saveStaff(@ModelAttribute User user, RedirectAttributes ra) {
        user.setFeeSummaryId("NA");
        user.setRoomId("NA");
        user.setStatus("ACTIVE");
        userService.saveUser(user);

        ra.addFlashAttribute("successMessage", "New " + user.getRole() + " registered successfully! ✅");
        return "redirect:/owner/dashboard";
    }

    @GetMapping("/approval-pending-payments")
    public String viewPendingPayments(Model model) {
        List<PaymentApprovalDTO> pending = paymentService.getPendingApprovals();
        model.addAttribute("pendingPayments", pending);
        model.addAttribute("mainContent", "owner-pages/pending-payments");
        return "manager-pages/layout";
    }

    @PostMapping("/approve-payment/{id}")
    public String processApproval(@PathVariable String id, @RequestParam String action,
                                  RedirectAttributes ra) {
        try {
            if ("APPROVE".equals(action)) {
                paymentService.approveAndBalance(id, "Owner_Bannu"); // Replace with actual session Owner name
                ra.addFlashAttribute("successMessage", "Payment Approved! Fee Summary Updated.");
            } else {
                paymentService.rejectPayment(id);
                ra.addFlashAttribute("errorMessage", "Payment rejected and cleared from queue.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/pending-payments";
    }

    @GetMapping("/expenses")
    public String viewExpenses(Model model, @RequestParam(required = false) String month) {

        if (month == null) {
            month = YearMonth.now().toString(); // Default to current month
        }
        // Fetch all records sorted by date
        List<Expenses> allExpenses = expensesService.findAllExpenses(month);

        model.addAttribute("expenses", allExpenses);
        model.addAttribute("selectedMonth", month);
        model.addAttribute("mainContent", "manager-pages/expenses-list");
        return "manager-pages/layout";
    }

    @PostMapping("/delete-expense/{id}")
    public String deleteExpense(@PathVariable String id, RedirectAttributes redirectAttributes) {
        expensesService.deleteExpense(id);
        redirectAttributes.addFlashAttribute("successMessage", "Expense record deleted. 🗑️");
        return "redirect:/manager/expenses";
    }
}
