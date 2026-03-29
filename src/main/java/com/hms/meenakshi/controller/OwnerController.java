package com.hms.meenakshi.controller;

import com.hms.meenakshi.dto.PaymentApprovalDTO;
import com.hms.meenakshi.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OwnerController {

    private  final PaymentService paymentService;

    @GetMapping("/pending-payments")
    public String viewPendingPayments(Model model) {
        List<PaymentApprovalDTO> pending = paymentService.getPendingApprovals();
        model.addAttribute("pendingPayments", pending);
        model.addAttribute("mainContent", "owner-pages/pending-payments");
        return "manager-pages/layout";
    }

    @PostMapping("/approve-payment/{id}")
    public String processApproval(@PathVariable String id,
                                  @RequestParam String action,
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
}
