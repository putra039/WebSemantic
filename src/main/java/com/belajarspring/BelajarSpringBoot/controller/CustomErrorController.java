package com.belajarspring.BelajarSpringBoot.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // tambahkan logika untuk menangani error di sini
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
