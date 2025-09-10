package com.soumen.kalpavriksha.Controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController
{
    @GetMapping("/run-test")
    public String runTest()
    {
        return "Test run successfully";
    }
}
