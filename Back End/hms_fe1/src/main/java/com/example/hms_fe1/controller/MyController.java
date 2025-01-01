package com.example.hms_fe1.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.hms_fe1.entity.FeePayment;
import com.example.hms_fe1.entity.FeeReceipt;
import com.example.hms_fe1.entity.IssueEntity;
import com.example.hms_fe1.entity.MappingEntity;
import com.example.hms_fe1.entity.Room;
import com.example.hms_fe1.entity.UserEntity;
import com.example.hms_fe1.repository.IssueRepository;
import com.example.hms_fe1.service.FeePaymentService;
import com.example.hms_fe1.service.FeeReceiptService;
import com.example.hms_fe1.service.IssueService;
import com.example.hms_fe1.service.MappingService;
import com.example.hms_fe1.service.RoomService;
import com.example.hms_fe1.service.StudentService;
import com.example.hms_fe1.service.UserService;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class MyController {

// ------------------------------------------------------------- <Required Beans> --------------------------------------------------------------------------- //

//  @Autowired
// 	private StudentService studentService;    
//  
	@Autowired
	private RoomService roomService;

	@Autowired
	private FeePaymentService feePaymentService;

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String from;

	@Autowired
	private UserService userService;

	@Autowired
	private MappingService mappingService;

	@Autowired
	private IssueService issueService;

	@Autowired
	private FeeReceiptService feeReceiptService;

// ------------------------------------------------------------- <Required Beans/> -------------------------------------------------------------------------- // 

// ---------------------------------------------------------------- <Home Routes> -------------------------------------------------------------------------- // 

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

// ---------------------------------------------------------------- <Home Routes/> -------------------------------------------------------------------------- // 

// ---------------------------------------------------------------- <Admin Contact> ------------------------------------------------------------------------- //	

	@PostMapping("/contact-admin")
	public String contactAdmin(HttpServletRequest request) {
		String name = request.getParameter("name");
		String email = request.getParameter("email");
		String subject = request.getParameter("subject");
		String message = request.getParameter("message");
		String to = "2100030959cseh@gmail.com";

		try {
			// Read HTML email template
			Resource resource = new ClassPathResource("templates/ContactMailTemplate.html");
			String emailTemplate = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);

			// Replace placeholders in the template with user-provided data
			String emailBody = emailTemplate.replace("{{name}}", name).replace("{{email}}", email)
					.replace("{{subject}}", subject).replace("{{message}}", message);

			// Create MimeMessage
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(from); // Replace `from` with the actual sender email address
			helper.setTo(to); // Replace `to` with the recipient email address (admin's email)
			helper.setSubject("New Contact Form Submission."); // Dynamic subject line
			helper.setText(emailBody, true); // `true` indicates HTML content

			// Send the email
			mailSender.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
			// Log the exception or handle it accordingly
			return "error"; // Redirect to an error page if needed
		}

		return "redirect:/"; // Redirect to a success page or confirmation view
	}

// ---------------------------------------------------------------- <Admin Contact/> ------------------------------------------------------------------------ //	

// ---------------------------------------------------------------- <User Verification> --------------------------------------------------------------------- //    
	@PostMapping("/save")
	public String saveUser(@ModelAttribute UserEntity userEntity, HttpServletRequest request, HttpSession session,
			Model model) {
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

	private void sendOTP(String regEmail, HttpSession session) {
		// Code to send OTP via email using mailsender
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

// ---------------------------------------------------------------- <User Verification/> -------------------------------------------------------------------- // 

// ---------------------------------------------------------------- <User Validation> --------------------------------------------------------------------- // 	

	@PostMapping("/user")
	public String userPanel(HttpServletRequest request, RedirectAttributes redirectAttributes, Model model,
			HttpSession session) {
		List<UserEntity> allUsers = userService.getAllUsers();
		boolean userFound = false;
		for (UserEntity ue : allUsers) {
			String id = request.getParameter("id");
			String pwd = request.getParameter("pwd");
			String email = id.concat("@kluniversity.in");
			if (ue.getMail().equals(email) && ue.getPassword().equals(pwd)) {
				Room room = roomService.getRoomByStuId(id);
				FeePayment feePayment = feePaymentService.getFeeByStuId(id);
				session.setAttribute("id", id);
				session.setAttribute("mail", email);
				session.setAttribute("name", ue.getName());
				session.setAttribute("room_id", room.getRoom_number());
				session.setAttribute("room_type", room.getRoom_type());
				session.setAttribute("deadline", feePayment.getNext_due_date());
				session.setAttribute("last_date", feePayment.getLast_paid_date());
				session.setMaxInactiveInterval(30 * 60);
				userFound = true;
				return "redirect:/student";
			} else if (ue.getMail().equals(email) && !ue.getPassword().equals(pwd)) {
				redirectAttributes.addFlashAttribute("successMessage", "Invalid Password..❌❌");
				return "redirect:/sign-in";
			}
		}

		if (!userFound) {
			redirectAttributes.addFlashAttribute("successMessage", "User Not Found..❌\nPlease Register First");
			return "redirect:/sign-up";
		}
		redirectAttributes.addFlashAttribute("successMessage", "Invalid Credentials..❌❌");
		return "redirect:/sign-in";
	}

// ---------------------------------------------------------------- <User Validation/> --------------------------------------------------------------------- // 	

// ---------------------------------------------------------------- <Student Routes> ------------------------------------------------------------------------ //

	@GetMapping("/student")
	public String home(HttpSession session, Model model) {
		String id = (String) session.getAttribute("id"); // Retrieve user id from session
		String name = (String) session.getAttribute("name");
		String mail = (String) session.getAttribute("mail");
		String room_id = (String) session.getAttribute("room_id");
		String room_type = (String) session.getAttribute("room_type");
		LocalDate deadline = (LocalDate) session.getAttribute("deadline");
		LocalDate last_date = (LocalDate) session.getAttribute("last_date");
		model.addAttribute("id", id); // Add user id to model
		model.addAttribute("name", name);
		model.addAttribute("mail", mail);
		model.addAttribute("room_id", room_id);
		model.addAttribute("room_type", room_type);
		model.addAttribute("deadline", deadline);
		model.addAttribute("last_date", last_date);
		return "dashboard";
	}

	@GetMapping("/accommodation")
	public String accommodation(HttpSession session, Model model) {

		String name = (String) session.getAttribute("name");
		String room_id = (String) session.getAttribute("room_id");
		String room_type = (String) session.getAttribute("room_type");
		model.addAttribute("studentName", name);
		model.addAttribute("roomNumber", room_id);
		model.addAttribute("roomType", room_type);
		if (room_type.equals("4_bed_non_ac") || room_type.equals("4_bed_ac"))
			model.addAttribute("washRoom", "Attached Washroom");
		else
			model.addAttribute("washRoom", "Shared Washroom");
		model.addAttribute("mainContent", "accommodation");
		return "layout";

	}

	@GetMapping("/contacts")
	public String accommodation(Model model) {
		model.addAttribute("mainContent", "contacts");
		return "layout";
	}

	@GetMapping("/issues")
	public String issues(Model model) {
		model.addAttribute("mainContent", "issues");
		return "layout";
	}

	@PostMapping("/issue")
	public String issue(@ModelAttribute IssueEntity issue, HttpSession session, RedirectAttributes redirectAttributes) {
		String id = (String) session.getAttribute("id");
		String room_id = (String) session.getAttribute("room_id");
		issue.setStatus("Pending");
		issue.setDate(LocalDate.now());
		issue.setStudentId(id);
		issue.setRoomNumber(room_id);
		issueService.saveIssue(issue);
		redirectAttributes.addFlashAttribute("successMessage", "Issue Raised Successfully..");
		return "redirect:/student";
	}

	@GetMapping("/issues-list")
	public String issuesList(HttpSession session, Model model) {
		if (session.getAttribute("mail") == null) {
			return "redirect:/sign-in";
		}
		String id = (String) session.getAttribute("id");
		Set<IssueEntity> userissues = issueService.getIssuesOfUser(id);
		model.addAttribute("issues", userissues);
		model.addAttribute("mainContent", "IssuesList");
		return "layout";
	}

	@GetMapping("/laundry")
	public String dashboard(Model model) {
		model.addAttribute("mainContent", "laundry");
		return "layout";
	}

	@PostMapping("/submit-laundry")
	public String submitLaundry(HttpServletRequest request, RedirectAttributes redirectAttributes,
			HttpSession session) {
		int pantsCount = Integer.parseInt(request.getParameter("pants-count"));
		int shirtsCount = Integer.parseInt(request.getParameter("shirts-count"));
		int towelsCount = Integer.parseInt(request.getParameter("towels-count"));
		int bedsheetsCount = Integer.parseInt(request.getParameter("bedsheets-count"));
		int othersCount = Integer.parseInt(request.getParameter("others-count"));
		int totalCount = pantsCount + shirtsCount + towelsCount + bedsheetsCount + othersCount;

		String to = (String) session.getAttribute("mail");
//	    String to = "2100030959cseh@gmail.com";

		try {
			// Read HTML email template
			Resource resource = new ClassPathResource("templates/LaundryMailTemplate.html");
			String emailTemplate = new String(Files.readAllBytes(resource.getFile().toPath()), StandardCharsets.UTF_8);

			// Replace placeholders in the template with user-provided data
			String emailBody = emailTemplate.replace("{{pantsCount}}", String.valueOf(pantsCount))
					.replace("{{shirtsCount}}", String.valueOf(shirtsCount))
					.replace("{{towelsCount}}", String.valueOf(towelsCount))
					.replace("{{bedsheetsCount}}", String.valueOf(bedsheetsCount))
					.replace("{{othersCount}}", String.valueOf(othersCount))
					.replace("{{totalCount}}", String.valueOf(totalCount));

			// Create MimeMessage
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			helper.setFrom(from); // Replace `from` with the actual sender email address
			helper.setTo(to); // Replace `to` with the recipient email address
			helper.setSubject("New Laundry Submission"); // Subject line
			helper.setText(emailBody, true); // `true` indicates HTML content

			// Send the email
			mailSender.send(mimeMessage);
		} catch (Exception e) {
			e.printStackTrace();
			// Log the exception or handle it accordingly
			return "error"; // Redirect to an error page if needed
		}
		redirectAttributes.addFlashAttribute("successMessage",
				"Laundry clothes count report sent to registered email.");
		return "redirect:/student"; // Redirect to a success page or confirmation view
	}

	@GetMapping("/profile")
	public String profile(Model model, HttpSession session) {
		if (session.getAttribute("mail") == null) {
			return "redirect:/sign-in";
		}
		String email = (String) session.getAttribute("mail");

		UserEntity ue = userService.getByEmail(email);
		model.addAttribute("user", ue);
		model.addAttribute("mainContent", "profile");
		return "layout";
	}

	@PostMapping("/update-profile")
	public String updateUser(@ModelAttribute("user") UserEntity user, HttpSession session,
			RedirectAttributes redirectAttributes) {
		if (session.getAttribute("mail") == null) {
			return "redirect:/sign-in";
		}
		String idStr = (String) session.getAttribute("id"); // Retrieve user id from session
		long id = Long.parseLong(idStr);
		String mail = (String) session.getAttribute("mail");
		user.setId(id);
		user.setMail(mail);
		userService.saveUser(user);
		redirectAttributes.addFlashAttribute("successMessage",
				"User Updated Successfully..✔\nPlease login with Updated Credentials");
		session.invalidate();
		return "redirect:/sign-in";
	}

	@GetMapping("/add-receipts")
	public String uploadReceipt(Model model) {
		model.addAttribute("mainContent", "ReceiptUploadForm");
		return "layout";

	}

	@PostMapping("/upload-fee-receipt")
	public ResponseEntity<String> uploadFeeReceipt(HttpSession session, @RequestParam("date") String date,
			@RequestParam("amount") String paymentAmount, @RequestParam("imageFile") MultipartFile receiptImage) {
		String studentId = (String) session.getAttribute("id");
		try {
			feeReceiptService.saveFeeReceipt(studentId, date, paymentAmount, receiptImage);
			return ResponseEntity.status(HttpStatus.OK).body("Fee receipt uploaded successfully");
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload fee receipt");
		}
	}
	
	@GetMapping("/receipts-list")
	public String receiptsList(HttpSession session, Model model) {
		if (session.getAttribute("mail") == null) {
			return "redirect:/sign-in";
		}
		String id = (String) session.getAttribute("id");
		Set<FeeReceipt> userReceipts = feeReceiptService.findStuReceipts(id);
		model.addAttribute("receipts", userReceipts);
		model.addAttribute("mainContent", "ReceiptsList");
		return "layout";
	}
	
	
	@GetMapping("/api/receipts/{id}")
	@ResponseBody
	public ResponseEntity<byte[]> getReceiptImage(@PathVariable long id) {
	    Optional<FeeReceipt> receiptOptional = feeReceiptService.findById(id);
	    if (receiptOptional.isPresent()) {
	        FeeReceipt receipt = receiptOptional.get();
	        byte[] imageData = receipt.getReceiptImage();

	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.IMAGE_JPEG);
	        return new ResponseEntity<>(imageData, headers, HttpStatus.OK);
	    }
	    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

// ---------------------------------------------------------------- <Student Routes/> ---------------------------------------------------------------------- //    

// ---------------------------------------------------------------- <SuperVisor Routes> -------------------------------------------------------------------- //    v
	@GetMapping("/add-users")
	public String addUsers() {
		return "MappingForm";
	}

	@PostMapping("/save-mapping")
	public String saveMapping(@ModelAttribute MappingEntity mappingEntity, HttpServletRequest request) {
		// Save student, room, and fee payment records
		mappingService.saveMappings(mappingEntity);
		return "dashboard";
	}

// ---------------------------------------------------------------- <SuperVisor Routes/> ------------------------------------------------------------------ //	

}
