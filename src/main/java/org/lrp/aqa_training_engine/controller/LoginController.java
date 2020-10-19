package org.lrp.aqa_training_engine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Model model,
                        @RequestParam(value = "auth_error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout) {
        if (error != null) {
            model.addAttribute("authError", "Your username and/or password are invalid.");
        }

        if (logout != null) {
            model.addAttribute("successfulLogoutMsg", "You have been logged out successfully.");
        }

        return "login";
    }
}
