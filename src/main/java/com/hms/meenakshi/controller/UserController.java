package com.hms.meenakshi.controller;

import com.hms.meenakshi.entity.Room;
import com.hms.meenakshi.service.RoomService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@AllArgsConstructor
public class UserController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

}
