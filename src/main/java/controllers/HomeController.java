package com.paulo.api_spring_jwt.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/home")
public class HomeController {

//    @GetMapping("/hello")
//    public String hello(Authentication auth) {
//        return "Hello "+auth.getName();
//    }


    @GetMapping("/hello")
    public String hello() {
        return "Hello ";
    }
}