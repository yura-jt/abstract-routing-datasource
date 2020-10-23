package com.examine.connector.controller;

import com.examine.connector.service.GreetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class HelloWorldController {
    private final GreetingService greetingService;

    @GetMapping(value = "/hello1")
    @ResponseBody
    public String hello1() {
        return greetingService.getGreetingFromFirstDb();
    }

    @GetMapping(value = "/hello2")
    @ResponseBody
    public String hello2() {
        return greetingService.getGreetingFromSecondDb();
    }

}
