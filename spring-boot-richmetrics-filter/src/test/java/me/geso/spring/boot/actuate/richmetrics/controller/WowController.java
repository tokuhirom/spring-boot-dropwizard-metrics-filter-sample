package me.geso.spring.boot.actuate.richmetrics.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WowController {
    @RequestMapping("/wow/hello")
    public String wow() {
        return "wow! hello";
    }
}
