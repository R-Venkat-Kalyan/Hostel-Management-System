package com.hms.meenakshi.controller;

import com.hms.meenakshi.entity.User;
import com.hms.meenakshi.repository.UserRepository;
import com.hms.meenakshi.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

     private final AuthService authService;
     private final UserRepository userRepository;

    /* STEP 1: Render the Current Password Check Page */
    @GetMapping("/update-password")
    public String showCurrentPasswordPage(HttpSession session, RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return "auth-current";
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-in";
    }

    /**
     * STEP 2: Verify Current Password & Trigger Email
     */
    @PostMapping("/verify-current")
    public String verifyCurrent(@RequestParam String currentPassword,
                                HttpSession session,
                                RedirectAttributes ra) {
        User user = (User) session.getAttribute("user");
        if (user != null) {

            // Validate current password
            if (!user.getPassword().equals(currentPassword)) {
                ra.addFlashAttribute("error", "The current password you entered is incorrect. ❌");
                return "redirect:/auth/update-password";
            }

            try {
                String otp = authService.generateOTP();
                session.setAttribute("tempOTP", otp); // Store OTP in session
                authService.sendOtpMail(user.getEmail(), otp, user.getFullName());

                return "redirect:/auth/reset-password";
            } catch (Exception e) {
                e.printStackTrace();
                ra.addFlashAttribute("error", "Email service failed. Please try again later.");
                return "redirect:/auth/update-password";
            }
        }
        ra.addFlashAttribute("successMessage", "User Session Expired, LogIn Again. ❌");
        return "redirect:/sign-in";
    }

    /**
     * STEP 3: Render the Combined OTP + New Password Page
     */
    @GetMapping("/reset-password")
    public String showResetPage(HttpSession session) {
        if (session.getAttribute("tempOTP") == null) return "redirect:/auth/update-password";
        return "auth-reset";
    }

    /**
     * STEP 4: Final Validation & DB Update
     */
    @PostMapping("/final-update")
    public String finalUpdate(@RequestParam String otp,
                              @RequestParam String newPass,
                              @RequestParam String confirmPass,
                              HttpSession session,
                              RedirectAttributes ra) {

        User user = (User) session.getAttribute("user");
        String sessionOtp = (String) session.getAttribute("tempOTP");

        // 1. Validate OTP
        if (sessionOtp == null || !otp.equals(sessionOtp)) {
            ra.addFlashAttribute("error", "Invalid or expired OTP code. ❌");
            return "redirect:/auth/reset-password";
        }

        // 2. Validate Password Match
        if (!newPass.equals(confirmPass)) {
            ra.addFlashAttribute("error", "New & Confirm password do not match. ❌");
            return "redirect:/auth/reset-password";
        }

        // 3. Validate it's not the same as old
        if (newPass.equals(user.getPassword())) {
            ra.addFlashAttribute("error", "New password cannot be the same as your current one. ❌");
            return "redirect:/auth/reset-password";
        }

        // 4. Update Database
        user.setPassword(newPass);
        userRepository.save(user);

        // 5. Clean up & Logout
        session.invalidate();
        ra.addFlashAttribute("successMessage", "Security updated! Please log in with your new password. ✅");

        return "redirect:/sign-in";
    }

    @PostMapping("/forgot-password")
    public String forgotPasswordInit(@RequestParam String email, HttpSession session, RedirectAttributes ra) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            ra.addFlashAttribute("successMessage", "No account found with this email! ❌");
            return "redirect:/sign-in";
        }

        User user = userOpt.get();

        try {
            String otp = authService.generateOTP();
            // Set the user in session so 'final-update' knows who to update
            session.setAttribute("user", user);
            session.setAttribute("tempOTP", otp);

            authService.sendOtpMail(user.getEmail(), otp, user.getFullName());

            // Reusing your existing reset page!
            return "redirect:/auth/reset-password";
        } catch (Exception e) {
            ra.addFlashAttribute("successMessage", "Email service failed. Try again.");
            return "redirect:/sign-in";
        }
    }
}