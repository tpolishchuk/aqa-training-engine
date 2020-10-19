package org.lrp.aqa_training_engine.controller;

import org.lrp.aqa_training_engine.model.User;
import org.lrp.aqa_training_engine.service.SecurityService;
import org.lrp.aqa_training_engine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    @GetMapping("/registration")
    public String registration(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@Valid @ModelAttribute("user") User user,
                               Model model,
                               Errors errors,
                               RedirectAttributes redirectAttributes) {

        if (errors != null && errors.getErrorCount() > 0) {
            return "registration";
        }

        if (!user.getPassword().equals(user.getPasswordConfirm())) {
            model.addAttribute("generalError",
                               "Password confirmation and password should be equal");
            return "registration";
        }

        if (userService.findByUsername(user.getUsername()) != null) {
            model.addAttribute("generalError",
                               "User with a given username already exists");
            return "registration";
        }

        userService.save(user);

        securityService.login(user.getUsername(), user.getPasswordConfirm());

        //TODO: to messages
        redirectAttributes.addFlashAttribute("successfulRegMsg",
                                             "Thanks for the registration! You are successfully logged in");

        return "redirect:/home";
    }
}
