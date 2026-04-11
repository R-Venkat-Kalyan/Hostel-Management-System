package com.hms.meenakshi.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.hms.meenakshi.entity.UserReceipts;
import com.hms.meenakshi.repository.UserReceiptsRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserReceiptsService {

    private final UserReceiptsRepository receiptsRepository;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    private Cloudinary cloudinary;

    @PostConstruct
    public void init() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    public void saveReceipt(MultipartFile file, String userId, Double amount, LocalDate date) throws IOException {
        // Upload logic
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap(
                        "folder", "meenakshi_pg/receipts/" + userId,
                        "resource_type", "auto"
                ));

        UserReceipts receipt = new UserReceipts();
        receipt.setUserId(userId);
        receipt.setAmount(amount);
        receipt.setPaymentDate(date);
        receipt.setReceiptUrl(uploadResult.get("secure_url").toString());
        receipt.setPublicId(uploadResult.get("public_id").toString());

        receiptsRepository.save(receipt);
    }

    public List<UserReceipts> getReceiptsForUser(String userId) {
        return receiptsRepository.findByUserIdOrderByPaymentDateDesc(userId);
    }

    public boolean deleteReceipt(String id) {
        return receiptsRepository.findById(id).map(receipt -> {
            try {
                // Delete from Cloudinary
                cloudinary.uploader().destroy(receipt.getPublicId(), ObjectUtils.emptyMap());
                // Delete from DB
                receiptsRepository.delete(receipt);
                return true;
            } catch (IOException e) {
                return false;
            }
        }).orElse(false);
    }

    public Optional<UserReceipts> findById(String id) {
        return receiptsRepository.findById(id);
    }

    @Async
    public void deleteAllReceiptsByUserId(String userId) {
        try {
            // 1. Find all records for this user
            List<UserReceipts> receipts = getReceiptsForUser(userId);

            // 2. Loop through and delete from Cloudinary
            for (UserReceipts receipt : receipts) {
                // Assuming you store the Cloudinary Public ID in your entity
                if (receipt.getPublicId() != null) {
                    cloudinary.uploader().destroy(receipt.getPublicId(), ObjectUtils.emptyMap());
                }
            }

            // 3. Clear the database records
            receiptsRepository.deleteByUserId(userId);

            System.out.println("Async Cleanup: Cloudinary assets and DB records cleared for user: " + userId);
        } catch (Exception e) {
            // Since this is Async, we log the error because the controller won't see it
            System.err.println("Failed to perform async Cloudinary cleanup: " + e.getMessage());
        }
    }
}
