package org.lrp.aqa_training_engine.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Model model,
                       HttpServletRequest request) {
        Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);

        if (inputFlashMap != null &&
            inputFlashMap.containsKey("successfulRegMsg")) {
            model.addAttribute("successfulRegMsg", inputFlashMap.get("successfulRegMsg"));
        }

        return "home";
    }
}
