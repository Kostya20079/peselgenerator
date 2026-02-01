package com.peselgenerator.controller;

import com.peselgenerator.dto.GeneratePeselRequest;
import com.peselgenerator.service.PeselService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@AllArgsConstructor
public class HomeController {

    private final PeselService peselService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("generateRequest", new GeneratePeselRequest());
        return "index";
    }

    @PostMapping("/generate-single")
    public String generateSingle(@Valid GeneratePeselRequest request, BindingResult result, Model model) {

        // validation check
        if (result.hasErrors()) {
            return "index";
        }

        try {
            String pesel = peselService.generateSinglePesel(request.getBirthDate(), request.getGender());
            model.addAttribute("pesel", pesel);
            model.addAttribute("success", "PESEL number has been created!");
        } catch (Exception e) {
            model.addAttribute("error", "Error while generation PESEL: " + e.getMessage());
        }

        model.addAttribute("generateRequest", new GeneratePeselRequest());
        return "index";
    }

}