package com.hms.meenakshi.service;

import com.hms.meenakshi.dto.PaymentApprovalDTO;
import com.hms.meenakshi.dto.PaymentHistoryDTO;
import com.hms.meenakshi.entity.FeeSummary;
import com.hms.meenakshi.entity.Payments;
import com.hms.meenakshi.entity.User;
import com.hms.meenakshi.repository.FeeSummaryRepository;
import com.hms.meenakshi.repository.PaymentRepository;
import com.hms.meenakshi.repository.RoomRepository;
import com.hms.meenakshi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepo;
    private final UserRepository userRepository;
    private final FeeSummaryRepository feeSummaryRepository;
    private final RoomRepository roomRepo;

    public void recordPendingPayment(String userId, double amountPaid, String mode, String ref, String managerId) {
        // 1. Create Payment Log
        Payments p = new Payments();
        p.setUserId(userId);
        p.setAmount(amountPaid);
        p.setPaymentMode(mode);
        p.setTransactionRef(ref);
        p.setPaidDate(LocalDateTime.now());
        p.setStatus("PENDING"); // Auto-approving if manager records it
        p.setCreatedBy(managerId);
        p.setCreatedAt(LocalDateTime.now());
        paymentRepo.save(p);

    }

    public List<PaymentHistoryDTO> getFilteredPayments(String monthStr) {
        // 1. Parse Month
        YearMonth yearMonth = (monthStr == null || monthStr.isEmpty())
                ? YearMonth.now() : YearMonth.parse(monthStr);

        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        return paymentRepo.findAll().stream()
                // Filter by date range
                .filter(p -> p.getPaidDate().isAfter(start) && p.getPaidDate().isBefore(end))
                .map(p -> {
                    PaymentHistoryDTO dto = new PaymentHistoryDTO();
                    dto.setId(p.getId());
                    dto.setAmount(p.getAmount());
                    dto.setPaymentMode(p.getPaymentMode());
                    dto.setTransactionRef(p.getTransactionRef());
                    dto.setPaidDate(p.getPaidDate());
                    dto.setStatus(p.getStatus());

                    userRepository.findById(p.getUserId()).ifPresent(u -> {
                        dto.setUserName(u.getFullName());
                        roomRepo.findById(u.getRoomId()).ifPresent(r -> dto.setRoomNumber(r.getRoomNumber()));
                    });
                    return dto;
                })
                // 2. Custom Sort: PENDING first, then REJECTED, then APPROVED
                .sorted((p1, p2) -> {
                    int score1 = getStatusPriority(p1.getStatus());
                    int score2 = getStatusPriority(p2.getStatus());
                    if (score1 != score2) return Integer.compare(score1, score2);
                    return p2.getPaidDate().compareTo(p1.getPaidDate()); // Newest first within status
                })
                .collect(Collectors.toList());
    }

    private int getStatusPriority(String status) {
        if ("PENDING".equals(status)) return 1;
        if ("REJECTED".equals(status)) return 2;
        return 3; // APPROVED
    }

    //    OWNER SERVICES
    // FETCH ALL PENDING FOR THE TABLE
    public List<PaymentApprovalDTO> getPendingApprovals() {
        return paymentRepo.findAll().stream()
                .filter(p -> "PENDING".equals(p.getStatus()))
                .map(p -> {
                    PaymentApprovalDTO dto = new PaymentApprovalDTO();
                    dto.setId(p.getId());
                    dto.setAmount(p.getAmount());
                    dto.setPaymentMode(p.getPaymentMode());
                    dto.setTransactionRef(p.getTransactionRef());
                    dto.setPaidDate(p.getPaidDate());
                    dto.setManagerName(p.getCreatedBy());

                    // Fetch User & Room Name
                    userRepository.findById(p.getUserId()).ifPresent(u -> {
                        dto.setUserName(u.getFullName());
                        roomRepo.findById(u.getRoomId()).ifPresent(r ->
                                dto.setRoomNumber(r.getRoomNumber())
                        );
                        feeSummaryRepository.findById(u.getFeeSummaryId()).ifPresent(f ->
                                dto.setDueAmount(f.getPendingAmount())
                        );
                    });

                    return dto;
                }).collect(Collectors.toList());
    }

    // THE APPROVAL LOGIC
    @Transactional
    public void approveAndBalance(String paymentId, String ownerId) {
        Payments p = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        // 1. Update Payment Record
        p.setStatus("APPROVED");
        p.setApprovedBy(ownerId);
        p.setApprovedDate(LocalDateTime.now());
        paymentRepo.save(p);

        // 2. Update Resident Fee Summary
        User user = userRepository.findById(p.getUserId()).orElseThrow();
        FeeSummary fee = feeSummaryRepository.findById(user.getFeeSummaryId()).orElseThrow();

        double newPaid = fee.getPaidAmount() + p.getAmount();
        fee.setPaidAmount(newPaid);
        fee.setPendingAmount(fee.getTotalAmount() - newPaid);
        fee.setLastPaymentDate(LocalDate.now());

        feeSummaryRepository.save(fee);
    }

    public void rejectPayment(String paymentId) {
        Payments p = paymentRepo.findById(paymentId).orElseThrow();
        p.setStatus("REJECTED");
        paymentRepo.save(p);
    }

    public List<Payments> getPaymentsByUserId(String userId) {
        return paymentRepo.getPaymentsByUserId(userId);
    }
}
