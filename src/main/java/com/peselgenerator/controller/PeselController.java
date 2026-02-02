package com.peselgenerator.controller;

import com.peselgenerator.dto.GeneratePeselRequest;
import com.peselgenerator.entity.User;
import com.peselgenerator.service.PeselService;
import com.peselgenerator.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class PeselController {

    private final PeselService peselService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userService.findByEmail(email);

        model.addAttribute("user", user);
        model.addAttribute("generateRequest", new GeneratePeselRequest());
        return "dashboard";
    }
}