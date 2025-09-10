package com.soumen.kalpavriksha.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class HomeController
{
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/")
    public String home()
    {
        return "Welcome to Kalpavriksha";
    }

}

