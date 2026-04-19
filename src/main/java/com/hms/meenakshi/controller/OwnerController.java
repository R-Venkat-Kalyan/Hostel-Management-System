package com.hms.meenakshi.controller;

import com.hms.meenakshi.dto.Expenses;
import com.hms.meenakshi.dto.PaymentApprovalDTO;
import com.hms.meenakshi.dto.PaymentHistoryDTO;
import com.hms.meenakshi.dto.ResidentDetailsDTO;
import com.hms.meenakshi.entity.Room;
import com.hms.meenakshi.entity.User;
import com.hms.meenakshi.entity.VacatedResident;
import com.hms.meenakshi.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/owner")
public class OwnerController {

    private final PaymentService paymentService;
    private final UserService userService;
    private final ExpensesService expensesService;
    private final RoomService roomService;
    private final VacatedResidentService vacatedResidentService;

    @GetMapping("/dashboard")
    public String dashBoard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        Boolean isVerified = (Boolean) session.getAttribute("isVerified");
        if (user != null) {
            // 1. Residing Students
            List<User> residents = userService.findByRole("RESIDENT"); // Adjust null if your logic uses "NEW" or a specific RoomId check
            long totalStudents = residents.size();

            // 2. Room & Bed Logic (Reuse your viewRooms logic)
            List<Room> rooms = roomService.getAllRooms();

            int availableRooms = (int) rooms.stream().filter(r -> r.getOccupiedCount() < r.getCapacity()).count();

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

            // Total Vacated Students
            long vacatedResidents = vacatedResidentService.totalVacatedStudents();

            // Adding to Model
            model.addAttribute("totalStudents", totalStudents);
            model.addAttribute("availableRooms", availableRooms);
            model.addAttribute("pendingApprovals", pendingApprovals);
            model.addAttribute("vacatedCount", vacatedResidents);
            model.addAttribute("last_date", collectedThisMonth); // Match HTML variable
            model.addAttribute("deadline", totalOutstanding);   // Match HTML variable

            model.addAttribute("mainContent", "owner-pages/dashboard");
            return "owner-pages/layout";
        }
//        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    @GetMapping("/add-staff")
    public String addStaff(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("mainContent", "owner-pages/add-staff");
            return "owner-pages/layout";
        }
//        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    @PostMapping("/save-staff")
    public String saveStaff(@ModelAttribute User user, RedirectAttributes ra, HttpSession session) {
        User loggedUser = (User) session.getAttribute("user");
        if (loggedUser != null) {
            user.setFeeSummaryId("NA");
            user.setRoomId("NA");
            user.setStatus("ACTIVE");
            userService.saveUser(user);

            ra.addFlashAttribute("successMessage", "New " + user.getRole() + " saved successfully! ✅");
            return "redirect:/owner/dashboard";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    @GetMapping("/view-staff")
    public String viewStaff(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<User> staff = userService.getAllStaffMembers();
            model.addAttribute("staffList", staff);
            model.addAttribute("mainContent", "owner-pages/view-staff");
            return "owner-pages/layout";
        }
//        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    @GetMapping("/edit-staff/{id}")
    public String editStaff(@PathVariable String id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            User staff = userService.findById(id).orElseThrow(() -> new RuntimeException("Staff not found"));
            // We pass the actual object so Thymeleaf can read its fields
            model.addAttribute("user", staff);
            model.addAttribute("mainContent", "owner-pages/add-staff");
            return "owner-pages/layout";
        }
//        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    @PostMapping("/delete-staff/{id}")
    public String deleteStaff(@PathVariable String id, RedirectAttributes ra, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            userService.deleteStaff(id);
            ra.addFlashAttribute("successMessage", "Staff member removed successfully. 🗑️");
            return "redirect:/owner/view-staff";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    @GetMapping("/approval-pending-payments")
    public String viewPendingPayments(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<PaymentApprovalDTO> pending = paymentService.getPendingApprovals();
            model.addAttribute("pendingPayments", pending);
            model.addAttribute("mainContent", "owner-pages/pending-payments");
            return "manager-pages/layout";
        }
//        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    @PostMapping("/approve-payment/{id}")
    public String processApproval(@PathVariable String id, @RequestParam String action,
                                  RedirectAttributes ra, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            try {
                if ("APPROVE".equals(action)) {
                    paymentService.approveAndBalance(id, user.getFullName()); // Replace with actual session Owner name
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
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
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
            model.addAttribute("mainContent", "owner-pages/expenses-list");
            return "owner-pages/layout";
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
            return "redirect:/owner/expenses";
        }
        redirectAttributes.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    @GetMapping("/vacated-residents")
    public String viewVacatedResidents(HttpServletRequest request, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<VacatedResident> vacatedList = vacatedResidentService.getAllVacatedResidents();
            model.addAttribute("vacatedResidents", vacatedList);

            // Calculate total historical revenue for the footer
            double totalHistoricalRevenue = vacatedList.stream()
                    .mapToDouble(VacatedResident::getTotalPaidDuringStay)
                    .sum();
            model.addAttribute("totalRevenue", totalHistoricalRevenue);
            model.addAttribute("mainContent", "owner-pages/vacated-residents");
            return "owner-pages/layout";
        }
//        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }
}
