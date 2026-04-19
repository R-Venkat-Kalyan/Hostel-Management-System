package com.hms.meenakshi.controller;

import com.hms.meenakshi.entity.FeedBack;
import com.hms.meenakshi.entity.Room;
import com.hms.meenakshi.entity.User;
import com.hms.meenakshi.service.FeedBackService;
import com.hms.meenakshi.service.RoomService;
import com.hms.meenakshi.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final FeedBackService feedBackService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/sign-in")
    public String signIn(){
        return "sign-in";
    }

    @PostMapping("/login")
    public String loginProcess(@RequestParam String email,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        // 1. Find user by email directly (much faster than a loop)
        Optional<User> userOpt = userService.findByEmail(email);

        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "User Not Found..❌\nContact your manager to get your credentials created!");
            return "redirect:/sign-in";
        }

        User user = userOpt.get();

        // 2. Check Password
        // Note: If you use BCrypt later, use: passwordEncoder.matches(password, user.getPassword())
        if (!user.getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("successMessage", "Invalid Password..❌");
            return "redirect:/sign-in";
        }

        // 3. Check Account Status
        if (!"ACTIVE".equals(user.getStatus())) {
            redirectAttributes.addFlashAttribute("successMessage", "Account Inactive..🔒 Contact Admin.");
            return "redirect:/sign-in";
        }

        // 4. Session Setup
        session.setAttribute("user", user);
        session.setMaxInactiveInterval(30 * 60); // 30 minutes

        // 5. Role-Based Redirection
        String role = user.getRole();
        if ("RESIDENT".equals(role)) {
            return "redirect:/resident/dashboard";
        } else {
            // OWNER and MANAGER both go to the same Security PIN page
            return "redirect:/auth/security-check";
        }
    }

    @GetMapping("/feedback")
    public String feedBack(){
        return "feedback";
    }

    @PostMapping("/save-feedback")
    public String saveFeedback(@ModelAttribute FeedBack feedback,
                               HttpSession session,
                               RedirectAttributes ra) {
        // 1. Fetch current user from session
        User user = (User) session.getAttribute("user");

        if (user != null) {
            // 2. Inject Role and Identity into the feedback object
            feedback.setUserId(user.getId());
            feedback.setUserName(user.getFullName());
            feedback.setUserRole(user.getRole());

            // 3. Save to MongoDB
            feedBackService.save(feedback);

            ra.addFlashAttribute("successMessage", "Thank you! Your feedback helps us grow. 🌱");
        }

        // 4. Smart Redirect based on role
        String redirectPath = "/resident/dashboard"; // Default
        if ("OWNER".equals(user.getRole())) redirectPath = "/owner/dashboard";
        if ("MANAGER".equals(user.getRole())) redirectPath = "/manager/dashboard";

        return "redirect:" + redirectPath;
    }

    @GetMapping("/all-contacts")
    public String contacts(){
        return "resident-pages/contacts";
    }

    @PostMapping("/sign-out")
    public String signOut(HttpSession session, Model model){
        session.invalidate();
//        model.addAttribute("successMessage", "Logged out successfully. See you soon! 👋");
//        model.addAttribute("successMessage", "Your session has ended. Please sign in to access your dashboard. 🔒");
        return "index";
    }

}
