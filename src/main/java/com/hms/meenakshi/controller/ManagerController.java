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
            return "redirect:/auth/security-challenge";
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

    @GetMapping("/add-expense")
    public String addExpense(Model model){
        model.addAttribute("mainContent", "add-expense");
        return "manager-pages/layout";
    }

    @PostMapping("/save-expense")
    public String saveExpense(@ModelAttribute Expenses expense,
                              HttpSession session,
                              RedirectAttributes redirectAttributes) {

        // 1. Get the logged-in user from the session
        User currentUser = (User) session.getAttribute("user");

        if (currentUser != null) {
            // 2. Set the audit fields before saving
            expense.setAddedBy(currentUser.getFullName());
            expense.setAddedByRole(currentUser.getRole());

            // 3. Persist to MongoDB
            expensesService.saveExpense(expense);

            redirectAttributes.addFlashAttribute("successMessage", "Expense logged successfully! ✅");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Session expired. Please login again.");
            return "redirect:/sign-in";
        }

        return "redirect:/manager/expenses";
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

    @GetMapping("/contacts")
    public String allContacts(Model model) {
        model.addAttribute("mainContent", "resident-pages/contacts");
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
        return "redirect:/manager/assign-room";
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

    @PostMapping("/vacate-resident/{id}")
    public String vacateResident(@PathVariable String id, RedirectAttributes ra) {
        try {
            // 'false' indicates this is a permanent exit
            assignmentService.handleResidentExit(id, "Course Completed / Voluntary Exit", false);
            ra.addFlashAttribute("successMessage", "Resident vacated and archived. Owner notified! ✅");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/manager/view-residents";
    }

    @PostMapping("/switch-room/{id}")
    public String switchRoom(@PathVariable String id, RedirectAttributes ra) {
        try {
            // 'true' indicates a switch; user is reset but not deleted
            assignmentService.handleResidentExit(id, "Internal Room Change", true);
            ra.addFlashAttribute("successMessage", "Old room cleared! You can now re-assign this resident. 🔁");
            return "redirect:/manager/assign-room";
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/manager/view-residents";
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
