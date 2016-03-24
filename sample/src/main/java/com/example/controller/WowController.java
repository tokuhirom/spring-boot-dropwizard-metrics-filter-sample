package com.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WowController {
    @RequestMapping("/wow/hello")
    public String wow() {
        return "wow! hello";
    }
}
