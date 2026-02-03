package com.peselgenerator.controller;

import com.peselgenerator.dto.RegisterRequest;
import com.peselgenerator.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller responsible for handling user authentication and registration views.
 */
@Controller
@AllArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * Displays the user registration form.
     *
     * @param model the UI model to bind the register request object.
     * @return the view name for registration.
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    /**
     * Processes the registration form submission.
     *
     * @param registerRequest the DTO containing user registration data.
     * @param result          binding result for validation errors.
     * @param model           the UI model for passing error messages.
     * @return a redirect to the login page on success, or the register view on error.
     */
    @PostMapping("/register")
    public String register(@Valid RegisterRequest registerRequest, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.register(registerRequest);
            return "redirect:/login?success=true";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        } catch (Exception e) {
            model.addAttribute("error", "System error!");
            return "register";
        }
    }

    /**
     * Displays the login form.
     *
     * @return the view name for login.
     */
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}