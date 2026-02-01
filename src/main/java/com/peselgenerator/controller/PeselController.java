package com.peselgenerator.controller;

import com.peselgenerator.service.PeselService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class PeselController {

    private final PeselService peselService;

}