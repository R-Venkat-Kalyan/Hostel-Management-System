package com.hms.meenakshi.controller;

import com.hms.meenakshi.dto.Expenses;
import com.hms.meenakshi.dto.PaymentHistoryDTO;
import com.hms.meenakshi.dto.ResidentDetailsDTO;
import com.hms.meenakshi.entity.Room;
import com.hms.meenakshi.entity.User;
import com.hms.meenakshi.service.*;
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
    private final ExpensesService expensesService;

    @GetMapping("/dashboard")
    public String dashBoard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Boolean isVerified = (Boolean) session.getAttribute("isVerified");
        if (user == null || isVerified == null || !isVerified) {
//            ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
            return "redirect:/sign-out";
        }
        // 1. Residing Students
        List<User> residents = userService.findByRole("RESIDENT"); // Adjust null if your logic uses "NEW" or a specific RoomId check
        long totalStudents = residents.size();

        // 2. Room & Bed Logic (Reuse your viewRooms logic)
        List<Room> rooms = roomService.getAllRooms();
        int totalOccupied = rooms.stream().mapToInt(Room::getOccupiedCount).sum();
        int totalCapacity = rooms.stream().mapToInt(Room::getCapacity).sum();

        int availableRooms = (int) rooms.stream().filter(r -> r.getOccupiedCount() < r.getCapacity()).count();
        int availableBeds = totalCapacity - totalOccupied;

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
        model.addAttribute("availableBeds", availableBeds);
        model.addAttribute("pendingApprovals", pendingApprovals);

        model.addAttribute("last_date", collectedThisMonth); // Match HTML variable
        model.addAttribute("deadline", totalOutstanding);   // Match HTML variable

        model.addAttribute("mainContent", "manager-pages/dashboard");
        return "manager-pages/layout";
    }


    @GetMapping("/expenses")
    public String viewExpenses(Model model, @RequestParam(required = false) String month, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
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
//        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    @PostMapping("/delete-expense/{id}")
    public String deleteExpense(@PathVariable String id, RedirectAttributes redirectAttributes, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            expensesService.deleteExpense(id);
            redirectAttributes.addFlashAttribute("successMessage", "Expense record deleted. 🗑️");
            return "redirect:/manager/expenses";
        }
        redirectAttributes.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }


}
