package com.soumen.kalpavriksha.Controllers;

import com.soumen.kalpavriksha.Models.ClassificationResponse;
import com.soumen.kalpavriksha.Models.LLMRequest;
import com.soumen.kalpavriksha.Models.PingResponse;
import com.soumen.kalpavriksha.Service.LLMService;
import com.soumen.kalpavriksha.Utills.Common;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/llm")
public class LLMController
{
    @Autowired
    private LLMService llmService;

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping()
    {
        System.out.println("Inside ping method in controller");

        Mono<PingResponse> pingResponse = llmService.ping();

        System.out.println("pingResponse : " + pingResponse.block());

        return new ResponseEntity<>(Response.success("Pinged successfully", pingResponse.block()), HttpStatus.OK);

    }

    @PostMapping("/ask-question")
    public ResponseEntity<Map<String, Object>> askQuestion(@RequestBody LLMRequest llmRequest)
    {
        System.out.println("Inside askQuestion method in controller");
        System.out.println("question : " + llmRequest.getQuestion());

        String question = llmRequest.getQuestion();

        if(Common.isNullOrEmpty(question))
        {
            return new ResponseEntity<>(Response.error("Please provide question"), HttpStatus.BAD_REQUEST);
        }

        // validate question
        Mono<ClassificationResponse> response = llmService.validateQuestion(question);

        ClassificationResponse classificationResponse = response.block();

        if(!classificationResponse.getIs_gardening_query().equalsIgnoreCase("true"))
        {
            return new ResponseEntity<>(Response.error("Question is not valid"), HttpStatus.BAD_REQUEST);
        }

        System.out.println("response : " + response.block());

        // if the question is valid call llm
        Mono<Map<String , Object>> llmResponse = llmService.askQuestion(question);

        System.out.println("llmResponse : " + llmResponse.block());

        Map<String , Object> output = llmResponse.block();

        System.out.println("output : " + output);

        if(!(boolean)output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success("Question asked successfully", output.get("data")), HttpStatus.OK);
    }
}
