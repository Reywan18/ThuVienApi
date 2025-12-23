package com.tlu.thuvien.api.controller.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Trỏ đến file templates/login.html
    }

    //Vào trang chủ chuyển hướng luôn đến login
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}
