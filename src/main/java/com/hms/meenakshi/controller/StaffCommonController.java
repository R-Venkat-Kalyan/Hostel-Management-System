package com.hms.meenakshi.controller;

import com.hms.meenakshi.dto.Expenses;
import com.hms.meenakshi.dto.PaymentHistoryDTO;
import com.hms.meenakshi.dto.ResidentDetailsDTO;
import com.hms.meenakshi.entity.Room;
import com.hms.meenakshi.entity.User;
import com.hms.meenakshi.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class StaffCommonController {

    private final ExpensesService expensesService;
    private final RoomService roomService;
    private final UserService userService;
    private final RoomAssignmentService assignmentService;
    private final PaymentService paymentService;

//  Helper to resolve the layout based on the current URL path
    private String resolveStaffLayout(HttpServletRequest request, Model model, String commonPage) {
        String uri = request.getRequestURI();
        String role = uri.startsWith("/owner") ? "owner" : "manager";

        model.addAttribute("role", role);
        // Points to templates/common-pages/ folder
        model.addAttribute("mainContent", "common-pages/" + commonPage);

        // Returns manager-pages/layout or owner-pages/layout
        return role + "-pages/layout";
    }

    @GetMapping({"/manager/add-expense", "/owner/add-expense"})
    public String addExpense(HttpServletRequest request, Model model) {
        return resolveStaffLayout(request, model, "add-expense");
    }

    @PostMapping({"/manager/save-expense", "/owner/save-expense"})
    public String saveExpense(@ModelAttribute Expenses expense, HttpSession session, HttpServletRequest request, RedirectAttributes ra) {

        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null) {
            String uri = request.getRequestURI();
            String role = uri.startsWith("/owner") ? "owner" : "manager";

            expense.setAddedBy(currentUser.getFullName());
            expense.setAddedByRole(currentUser.getRole());
            expensesService.saveExpense(expense);

            // Dynamically redirect back to /manager/expenses or /owner/expenses
            return "redirect:/" + role + "/expenses";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    // --- Contacts Logic ---
    @GetMapping({"/manager/contacts", "/owner/contacts"})
    public String allContacts(HttpServletRequest request, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return resolveStaffLayout(request, model, "contacts");
        }
//        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    // --- Room Management Logic ---
    @GetMapping({"/manager/add-room", "/owner/add-room"})
    public String addRoom(HttpServletRequest request, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return resolveStaffLayout(request, model, "add-room");
        }
//        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    @PostMapping({"/manager/save-room", "/owner/save-room"})
    public String saveRoom(@ModelAttribute Room room, HttpServletRequest request, RedirectAttributes ra, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            roomService.saveRoom(room);
            ra.addFlashAttribute("successMessage", "Room Added Successfully! 🏨");

            // Determine role to redirect to the correct "view-rooms" route
            String role = request.getRequestURI().contains("/owner") ? "owner" : "manager";
            return "redirect:/" + role + "/view-rooms";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    // --- Add Resident ---
    @GetMapping({"/manager/add-resident", "/owner/add-resident"})
    public String addResident(HttpServletRequest request, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return resolveStaffLayout(request, model, "add-resident");
        }
//        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    @PostMapping({"/manager/save-resident", "/owner/save-resident"})
    public String saveResident(@ModelAttribute User user, HttpServletRequest request, RedirectAttributes ra, HttpSession session) {
        User loggedUser = (User) session.getAttribute("user");
        if (loggedUser != null) {
            user.setRoomId("NEW");
            user.setFeeSummaryId("NEW");
            userService.saveUser(user);

            ra.addFlashAttribute("successMessage", "Resident Added Successfully! ✅");
            String role = request.getRequestURI().contains("/owner") ? "owner" : "manager";
            return "redirect:/" + role + "/view-residents";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    // --- Room Assignment ---
    @GetMapping({"/manager/assign-room", "/owner/assign-room"})
    public String showAssignPage(HttpServletRequest request, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<User> unassigned = userService.findByRoleAndRoomId("RESIDENT", "NEW");
            List<Room> available = roomService.getAvailableRooms();

            model.addAttribute("unassignedResidents", unassigned);
            model.addAttribute("availableRooms", available);

            return resolveStaffLayout(request, model, "assign-room");
        }
        //ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    @PostMapping({"/manager/create-assignment", "/owner/create-assignment"})
    public String processAssignment(@RequestParam String userId, @RequestParam String roomId,
                                    HttpServletRequest request, RedirectAttributes ra, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            try {
                assignmentService.assignRoomToResident(userId, roomId);
                ra.addFlashAttribute("successMessage", "Resident assigned successfully! 🔑");
            } catch (Exception e) {
                ra.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            }

            String role = request.getRequestURI().contains("/owner") ? "owner" : "manager";
            return "redirect:/" + role + "/assign-room";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }


    // --- Resident Directory ---
    @GetMapping({"/manager/view-residents", "/owner/view-residents"})
    public String viewResidents(HttpServletRequest request, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<ResidentDetailsDTO> residentList = userService.getAllResidentSnapshots();
            model.addAttribute("residents", residentList);
            double totalDue = residentList.stream().mapToDouble(ResidentDetailsDTO::getDuePayment).sum();
            model.addAttribute("totalDue", totalDue);

            return resolveStaffLayout(request, model, "view-residents");
        }
//      ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    // --- Collect Payment Form ---
    @GetMapping({"/manager/collect-payment/{userId}", "/owner/collect-payment/{userId}"})
    public String showPaymentForm(@PathVariable String userId, HttpServletRequest request, Model model, RedirectAttributes ra, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            String role = request.getRequestURI().contains("/owner") ? "owner" : "manager";
            try {
                ResidentDetailsDTO resident = userService.getSingleResidentSnapshot(userId);
                if (resident == null) {
                    ra.addFlashAttribute("successMessage", "Resident not found!");
                    return "redirect:/" + role + "/view-residents";
                }
                model.addAttribute("resident", resident);
                return resolveStaffLayout(request, model, "collect-payment");
            } catch (Exception e) {
                ra.addFlashAttribute("successMessage", "Error: " + e.getMessage());
                return "redirect:/" + role + "/view-residents";
            }
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    // --- Save Payment ---
    @PostMapping({"/manager/save-payment", "/owner/save-payment"})
    public String savePayment(@RequestParam String userId, @RequestParam double amount,
                              @RequestParam String paymentMode, @RequestParam(required = false) String transactionRef,
                              HttpSession session, HttpServletRequest request, RedirectAttributes ra) {

        User currentUser = (User) session.getAttribute("user");
        if (currentUser != null) {
            String collectorName = (currentUser != null) ? currentUser.getFullName() : "Unknown Staff";
            String role = request.getRequestURI().contains("/owner") ? "owner" : "manager";

            try {
                paymentService.recordPendingPayment(userId, amount, paymentMode, transactionRef, collectorName);
                ra.addFlashAttribute("successMessage", "Payment Record Submitted for Approval!");
                return "redirect:/" + role + "/view-payments";
            } catch (Exception e) {
                ra.addFlashAttribute("errorMessage", "Failed to save: " + e.getMessage());
                return "redirect:/" + role + "/collect-payment/" + userId;
            }
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    // --- Vacate Resident ---
    @PostMapping({"/manager/vacate-resident/{id}", "/owner/vacate-resident/{id}"})
    public String vacateResident(@PathVariable String id, HttpServletRequest request, RedirectAttributes ra, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            String role = request.getRequestURI().contains("/owner") ? "owner" : "manager";
            try {
                assignmentService.handleResidentExit(id, "Course Completed / Voluntary Exit", false);
                ra.addFlashAttribute("successMessage", "Resident vacated and archived successfully! ✅");
            } catch (Exception e) {
                ra.addFlashAttribute("successMessage", e.getMessage());
            }
            return "redirect:/" + role + "/view-residents";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }


    // --- Switch Room Logic ---
    @PostMapping({"/manager/switch-room/{id}", "/owner/switch-room/{id}"})
    public String switchRoom(@PathVariable String id, HttpServletRequest request, RedirectAttributes ra, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            String role = request.getRequestURI().contains("/owner") ? "owner" : "manager";
            try {
                // 'true' indicates a switch; user is reset but not deleted
                assignmentService.handleResidentExit(id, "Internal Room Change", true);
                ra.addFlashAttribute("successMessage", "Old room cleared! Ready for re-assignment. 🔁");
                return "redirect:/" + role + "/assign-room";
            } catch (Exception e) {
                ra.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect:/" + role + "/view-residents";
            }
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    // --- View Payments ---
    @GetMapping({"/manager/view-payments", "/owner/view-payments"})
    public String viewPayments(@RequestParam(required = false) String month, HttpServletRequest request, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            if (month == null) month = YearMonth.now().toString();

            List<PaymentHistoryDTO> history = paymentService.getFilteredPayments(month);

            model.addAttribute("payments", history);
            model.addAttribute("selectedMonth", month);
            return resolveStaffLayout(request, model, "view-payments");
        }
//        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }

    // --- View Rooms ---
    @GetMapping({"/manager/view-rooms", "/owner/view-rooms"})
    public String viewRooms(HttpServletRequest request, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<Room> rooms = roomService.getAllRooms();

            // Consistent sorting for both roles
            rooms.sort(Comparator.comparing(Room::getRoomNumber));

            // Calculate totals for the footer
            int totalOccupied = rooms.stream().mapToInt(Room::getOccupiedCount).sum();
            int totalCapacity = rooms.stream().mapToInt(Room::getCapacity).sum();
            int totalVacant = totalCapacity - totalOccupied;

            model.addAttribute("rooms", rooms);
            model.addAttribute("totalOccupied", totalOccupied);
            model.addAttribute("totalVacant", totalVacant);

            return resolveStaffLayout(request, model, "view-rooms");
        }
//        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-out";
    }
}
