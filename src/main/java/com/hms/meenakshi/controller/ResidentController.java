package com.hms.meenakshi.controller;

import com.hms.meenakshi.entity.*;
import com.hms.meenakshi.service.FeeSummaryService;
import com.hms.meenakshi.service.PaymentService;
import com.hms.meenakshi.service.RoomService;
import com.hms.meenakshi.service.UserReceiptsService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("resident")
@RequiredArgsConstructor
public class ResidentController {

    private final RoomService roomService;
    private final FeeSummaryService feeSummaryService;
    private final PaymentService paymentService;
    private final UserReceiptsService receiptsService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session, RedirectAttributes ra) {
        // 1. Session Auth Check
        User user = (User) session.getAttribute("user");
        if (user != null) {
            // 2. Personal Info
            model.addAttribute("name", user.getFullName());
            model.addAttribute("mail", user.getEmail());

            // 3. Accommodation Info (Unwrapping the Optional safely)
            if (user.getRoomId() != null) {
                Room room = roomService.getRoomById(user.getRoomId()).orElse(null);
                if (room != null) {
                    model.addAttribute("room_number", room.getRoomNumber());
                    model.addAttribute("room_type", room.getRoomType());
                } else {
                    setDummyRoom(model);
                }
            } else {
                setDummyRoom(model);
            }

            // 4. Financial Summary (Unwrapping the Optional safely)
            if (user.getFeeSummaryId() != null) {
                FeeSummary fee = feeSummaryService.getSummaryById(user.getFeeSummaryId()).orElse(null);
                if (fee != null) {
                    model.addAttribute("totalPaid", fee.getPaidAmount());
                    model.addAttribute("dueAmount", fee.getPendingAmount());

                    // Date Formatting (dd MMM yyyy -> e.g., 04 Apr 2026)
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

                    model.addAttribute("last_date", fee.getLastPaymentDate() != null ?
                            fee.getLastPaymentDate().format(formatter) : "N/A");

                    model.addAttribute("deadline", fee.getNextDueDate() != null ?
                            fee.getNextDueDate().format(formatter) : "Not Set");
                } else {
                    setDummyFees(model);
                }
            } else {
                setDummyFees(model);
            }

            // 5. Layout Rendering
            model.addAttribute("mainContent", "resident-pages/dashboard");
            return "resident-pages/layout";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-in";
    }

    // Helper methods to prevent UI from looking "broken" if data is missing
    private void setDummyRoom(Model model) {
        model.addAttribute("room_number", "Unassigned");
        model.addAttribute("room_type", "Pending");
    }

    private void setDummyFees(Model model) {
        model.addAttribute("totalPaid", 0.0);
        model.addAttribute("dueAmount", 0.0);
        model.addAttribute("last_date", "--/--/--");
        model.addAttribute("deadline", "TBD");
    }

    @GetMapping("/accommodation")
    public String accommodation(Model model, HttpSession session, RedirectAttributes ra){
        model.addAttribute("mainContent", "resident-pages/accommodation");
        // 1. Retrieve the user from the session
        User user = (User) session.getAttribute("user");

        // 2. Security Check
        if (user != null) {

            // 3. Fetch Room Data, We use .orElse(null) to unwrap the Optional safely for the UI
            if (user.getRoomId() != null) {
                Room room = roomService.getRoomById(user.getRoomId()).orElse(null);
                model.addAttribute("room", room);
            } else {
                // If no roomId is linked to user, passing null is fine because the HTML has fallback dummy text (?: '101')
                model.addAttribute("room", null);
            }

            // 4. Set the fragment and return layout
            model.addAttribute("mainContent", "resident-pages/accommodation");
            return "resident-pages/layout";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-in";
    }

    @GetMapping("/my-payments")
    public String myPayments(Model model, HttpSession session, RedirectAttributes ra){
        // 1. Retrieve the user from the session
        User user = (User) session.getAttribute("user");

        // 2. Security Check: Session timeout handling
        if (user != null) {


            // 3. Fetch Payments from Database
            List<Payments> paymentList = paymentService.getPaymentsByUserId(user.getId());

            // 4. Pass data to UI
            // If the list is empty, Thymeleaf's th:each simply won't render rows
            model.addAttribute("payments", paymentList);
            model.addAttribute("mainContent", "resident-pages/my-payments");
            return "resident-pages/layout";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-in";
    }

    @GetMapping("/add-fee-receipt")
    public String addFeeReceipt(Model model, HttpSession session, RedirectAttributes ra){
        // 1. Retrieve the user from the session
        User user = (User) session.getAttribute("user");

        // 2. Security Check: Session timeout handling
        if (user != null) {
            model.addAttribute("mainContent", "resident-pages/add-receipts");
            return "resident-pages/layout";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-in";
    }

    @PostMapping("/save-receipt")
    public String handleUpload(@RequestParam("paymentDate") String date,
                               @RequestParam("amount") Double amount,
                               @RequestParam("receiptImage") MultipartFile file,
                               HttpSession session, RedirectAttributes ra) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user != null) {

            receiptsService.saveReceipt(file, user.getId(), amount, LocalDate.parse(date));

            ra.addFlashAttribute("successMessage", "Receipt uploaded and secured! 🛡️");
            return "redirect:/resident/view-receipts";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-in";
    }

    @GetMapping("/view-receipts")
    public String viewReceipts(Model model, HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user != null) {

            List<UserReceipts> receipts = receiptsService.getReceiptsForUser(user.getId());
            model.addAttribute("receipts", receipts);

            model.addAttribute("mainContent", "resident-pages/view-receipts");
            return "resident-pages/layout";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-in";
    }

    @GetMapping("/delete-receipt/{id}")
    public String delete(@PathVariable String id, RedirectAttributes ra, HttpSession session) {
        // 1. Retrieve the user from the session
        User user = (User) session.getAttribute("user");

        // 2. Security Check: Session timeout handling
        if (user != null) {
            if (receiptsService.deleteReceipt(id)) {
                ra.addFlashAttribute("successMessage", "Receipt removed. 🗑️");
            } else {
                ra.addFlashAttribute("successMessage", "Error deleting record. ❌");
            }
            return "redirect:/resident/view-receipts";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-in";
    }

    @GetMapping("/add-laundry")
    public String addLaundry(Model model, HttpSession session, RedirectAttributes ra){
        // 1. Retrieve the user from the session
        User user = (User) session.getAttribute("user");

        // 2. Security Check: Session timeout handling
        if (user != null) {
            model.addAttribute("fullName", "Kalyan");
            model.addAttribute("mainContent", "resident-pages/add-laundry");
            return "resident-pages/layout";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-in";
    }

    @GetMapping("/contacts")
    public String contacts(Model model, HttpSession session, RedirectAttributes ra){
        // 1. Retrieve the user from the session
        User user = (User) session.getAttribute("user");

        // 2. Security Check: Session timeout handling
        if (user != null) {
            model.addAttribute("mainContent", "resident-pages/contacts");
            return "resident-pages/layout";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-in";
    }
}
