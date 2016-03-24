package com.example.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
    @RequestMapping("/hello")
    public String hello() {
        return "Hello!";
    }

    @RequestMapping("/hello/{id}")
    public String hello2(@PathVariable("id") long id) throws InterruptedException {
        Thread.currentThread().sleep(id);
        return "Hello " + id;
    }
}
