package com.example.hms_fe1.controller;

import java.nio.file.Files;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hms_fe1.entity.FeePayment;
import com.example.hms_fe1.entity.MappingEntity;
import com.example.hms_fe1.entity.Room;
import com.example.hms_fe1.entity.Student;
import com.example.hms_fe1.entity.UserEntity;
import com.example.hms_fe1.service.FeePaymentService;
import com.example.hms_fe1.service.MappingService;
import com.example.hms_fe1.service.RoomService;
import com.example.hms_fe1.service.StudentService;
import com.example.hms_fe1.service.UserService;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MyController {
	
	@GetMapping("/")
	public String index() {
		return "index";
	}
	
	@GetMapping("/sign-in")
	public String signIn() {
		return "login";
	}
	
	@GetMapping("/sign-up")
	public String signUp() {
		return "register";
	}
	
	@GetMapping("/student")
	public String home() {
		return "dashboard";
	}
	
	@GetMapping("/laundry")
    public String dashboard(Model model) {
        model.addAttribute("mainContent", "laundry");
        return "layout";
    }

    @GetMapping("/contacts")
    public String accommodation(Model model) {
        model.addAttribute("mainContent", "contacts");
        return "layout";
    }

    @GetMapping("/issues")
    public String fees(Model model) {
        model.addAttribute("mainContent", "issues");
        return "layout";
    }
    
    @GetMapping("/add-users")
    public String addUsers() {
    	return "MappingForm";
    }
    
//    @PostMapping("/save-mapping")
//    public String saveMapping(@ModelAttribute MappingEntity mappingEntity, HttpServletRequest request) {
//    	String id = request.getParameter("stu_id");
//    	String mail = id.concat("@kluniversity.in");
//    	int bal_amount = mappingEntity.getFee_amount() - mappingEntity.getAmount_paid();
//    	mappingEntity.setStu_mail(mail);
//    	mappingEntity.setBalance_amount(bal_amount);
//    	mappingService.saveMappings(mappingEntity);
//    	return "dashboard";
//    }
    
    @PostMapping("/save-mapping")
    public String saveMapping(@ModelAttribute MappingEntity mappingEntity, HttpServletRequest request) {
        // Save student, room, and fee payment records
        mappingService.saveMappings(mappingEntity);
        return "dashboard";
    }
    
    @Autowired
   	private StudentService studentService;    
    
    @Autowired
   	private RoomService roomService; 
    
    @Autowired
   	private FeePaymentService feePaymentService; 
    
    @Autowired
	private JavaMailSender mailSender;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MappingService mappingService;
	
	@PostMapping("/save")
	public String saveUser(@ModelAttribute UserEntity userEntity, HttpServletRequest request, HttpSession session, Model model) {
		String id = request.getParameter("id");
		String mail = id.concat("@kluniversity.in");
		userEntity.setMail(mail);
		session.setAttribute("user", userEntity);
		// Generate OTP and send email
		sendOTP(mail, session);
		model.addAttribute("mail", mail);
		return "OtpForm";
		
	}
	
	@GetMapping("/verify")
	public String verifyUser(@RequestParam("userOTP") String userOTP, HttpSession session,
	        RedirectAttributes redirectAttributes) {
	    int generatedOTP = (int) session.getAttribute("generatedOTP");

	    try {
	        int enteredOTP = Integer.parseInt(userOTP); // Convert combined OTP string to an integer

	        if (enteredOTP == generatedOTP) {
	            UserEntity userEntity = (UserEntity) session.getAttribute("user");
	            userService.saveUser(userEntity);
	            session.invalidate(); // Clear session after successful registration
	            redirectAttributes.addFlashAttribute("successMessage", "User registered successfully..✔\nLogin Now");
	            return "redirect:/sign-in";
	        } else {
	            redirectAttributes.addFlashAttribute("successMessage", "Invalid OTP..❌❌");
	            return "redirect:/sign-up"; // Redirect back to registration if OTP doesn't match
	        }
	    } catch (NumberFormatException e) {
	        redirectAttributes.addFlashAttribute("successMessage", "Invalid OTP format..❌❌");
	        return "redirect:/sign-up"; // Handle non-numeric OTP input
	    }
	}


	@Value("${spring.mail.username}")
    private String from;

	private void sendOTP(String regEmail, HttpSession session) {
		// Code to send OTP via email using mailsender
//		String from = "taskprompter@gmail.com";
		String to = regEmail;
		int generatedOTP = generateRandomOTP();
		session.setAttribute("generatedOTP", generatedOTP);
		try {
			// Read HTML email template
			Resource resource = new ClassPathResource("templates/OtpMailTemplate.html");
			String emailTemplate = new String(Files.readAllBytes(resource.getFile().toPath()));

			// Replace placeholder with generated OTP
			String emailBody = emailTemplate.replace("{{otp}}", String.valueOf(generatedOTP));

			// Create MimeMessage
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject("Verify email address for Meenakshi Boys Hostel");
			helper.setText(emailBody, true);

			// Send email
			mailSender.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
			// Handle exception
		}
	}

	private int generateRandomOTP() {
		Random random = new Random();
		return 100000 + random.nextInt(900000);
	}
	
	
	
	

}
