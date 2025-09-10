package com.soumen.kalpavriksha.Controllers;

import com.soumen.kalpavriksha.Models.HarvestRequest;
import com.soumen.kalpavriksha.Service.GeminiService;
import com.soumen.kalpavriksha.Utills.Common;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/harvest")
public class GeminiController
{
    @Autowired
    private GeminiService service;

    @PostMapping("/model/get-guide")
    public ResponseEntity<Map<String, Object>> getResponse(@RequestBody HarvestRequest request)
    {
        System.out.println("Inside gemini controller");

        String city =  request.getCity();
        String state =  request.getState();
        String country = request.getCountry();
        String crop =   request.getCrop();
        String latitude = request.getLatitude();
        String longitude = request.getLongitude();

        if(Common.isNullOrEmpty(crop))
        {
            return new ResponseEntity<>(Response.error("Please provide crop"), HttpStatus.BAD_REQUEST);
        }

        if(Common.isNullOrEmpty(city) || Common.isNullOrEmpty(state) || Common.isNullOrEmpty(country))
        {
            return new ResponseEntity<>(Response.error("Please provide city, state and country"), HttpStatus.BAD_REQUEST);
        }

        String location = "";

        if(Common.isNullOrEmpty(latitude) || Common.isNullOrEmpty(longitude))
        {
            location = city + ", " + state + ", " + country;
        }
        else
        {
            location = city + ", " + state + ", " + country + ", " + latitude + ", " + longitude;
        }

        String prompt =
                "You are a gardening expert.\n" +
                        "Always respond ONLY in valid JSON (no extra text, no explanations).\n\n" +

                        "Use this exact schema every time:\n\n" +
                        "{\n" +
                        "  \"task\": \"<short title of the activity>\",\n" +
                        "  \"location\": {\n" +
                        "    \"name\": \"<city, state, country or 'general' if not provided>\",\n" +
                        "    \"latitude\": \"<latitude if available, else null>\",\n" +
                        "    \"longitude\": \"<longitude if available, else null>\"\n" +
                        "  },\n" +
                        "  \"steps\": [\n" +
                        "    \"Step 1...\",\n" +
                        "    \"Step 2...\",\n" +
                        "    \"Step 3...\"\n" +
                        "  ],\n" +
                        "  \"tools_needed\": [\n" +
                        "    \"Tool 1\",\n" +
                        "    \"Tool 2\"\n" +
                        "  ],\n" +
                        "  \"precautions\": [\n" +
                        "    \"Precaution 1\",\n" +
                        "    \"Precaution 2\"\n" +
                        "  ]\n" +
                        "}\n\n" +

                        "Rules:\n" +
                        "- Always keep the same JSON keys in the same order.\n" +
                        "- If the user gives a location, include both name and coordinates (if known).\n" +
                        "- If location is missing, set name to \"general\" and latitude/longitude to null.\n" +
                        "- Write clear, step-by-step gardening instructions inside the steps array.\n" +
                        "- Do not output anything outside the JSON.\n\n" +

                        "Now fill this JSON based on the user query:\n" +
                        "\"how to harvest " + crop + " in my garden, location: " + location + "\"\n";


        Map<String, Object> response = service.geminiService(prompt);

        // System.out.println("Response : " + response.get("response"));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
