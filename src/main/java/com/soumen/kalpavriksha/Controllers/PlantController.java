package com.soumen.kalpavriksha.Controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.soumen.kalpavriksha.Auth.CustomUserDetails;
import com.soumen.kalpavriksha.Entity.UserProfile;
import com.soumen.kalpavriksha.Service.*;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/plant")
public class PlantController {
    @Autowired
    private PlantIdentification identification;

    @Autowired
    private PlantService service;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private HealthAssessment healthAssessment;

    @Autowired
    private TestService testService;

    @PostMapping("/identify/user/{userId}")
    public ResponseEntity<Map<String, Object>> identifyPlant(@RequestParam("image") MultipartFile image, @PathVariable String userId, Authentication authentication)
    {
        System.out.println("Inside identifyPlant method controller");

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        userId = customUserDetails.getUsername();

        System.out.println("userId inside identify plant controller : " + userId);

        if (image == null)
        {
            return new ResponseEntity<>(Map.of("error", "No image found"), HttpStatus.BAD_REQUEST);
        }

        if(userId.isEmpty() || userId == null)
        {
            return new ResponseEntity<>(Map.of("error", "User id not present, it is required"), HttpStatus.BAD_REQUEST);
        }

        String base64Image = null;
        try {
            base64Image = Base64.getEncoder().encodeToString(image.getBytes());
        } catch (IOException e) {
            System.out.println("Inside identifyPlant method exception inside controller");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        String[] images = new String[]{base64Image};

        System.out.println("Base64 string length: " + base64Image.length());

        if (images == null || images.length == 0) {
            return new ResponseEntity<>(Map.of("error", "No images found"), HttpStatus.BAD_REQUEST);
        }

        // Map<String , Object>output = identification.identifyPlants(images, userId);

        /* Test the UI */
         Map<String , Object> output = testService.getDetails(userId);

        System.out.println("output : " + output);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message").toString(), output.get("data")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success(output.get("data")), HttpStatus.OK);
    }

    @GetMapping("/get-all-identifications-history-for-a-user/user/{userId}")
    public ResponseEntity<Map<String, Object>> getAllIdentificationsHistoryForAUser(@PathVariable String userId)
    {
        Map<String , Object> output = service.getIdentificationHistory(userId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success(output.get("message").toString(),output.get("data")), HttpStatus.OK);

    }

    @GetMapping("/get-identification-history-details/identificationId/{identificationId}")
    public ResponseEntity<Map<String, Object>> getIdentificationHistoryDetails(@PathVariable String identificationId)
    {
        System.out.println("Inside getIdentificationHistoryDetails method in controller");
        System.out.println("identificationId : " + identificationId);

        Map<String, Object> output = service.getDetailsOfIdentifiedPlants(identificationId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success(output.get("message").toString(),output.get("data")), HttpStatus.OK);
    }

    @GetMapping("/get-advice")
    public ResponseEntity<Map<String, Object>> getAdvice(Authentication authentication, @RequestParam String crop)
    {
        System.out.println("Inside getAdvice method in controller");

        System.out.println("crop : " + crop);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUsername();

        System.out.println("userId : " + userId);

        if(userId == null)
        {
            return new ResponseEntity<>(Response.error("Bad request"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object> output = profileService.checkProfile(userId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message").toString(), output.get("data")), HttpStatus.BAD_REQUEST);
        }

        UserProfile userProfile = (UserProfile) output.get("data");

        System.out.println("userProfile : " + userProfile);

        try {
            output = geminiService.getModelResponse(userProfile, crop);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return new ResponseEntity<>(Response.success(output.get("message").toString(), output.get("data")), HttpStatus.OK);

    }

    @PostMapping("/detect-problem")
    public ResponseEntity<Map<String , Object>> detectProblem(Authentication authentication, @RequestParam("image") MultipartFile image)
    {
        System.out.println("Inside detectProblem method in controller");
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        if(customUserDetails == null)
        {
            return new ResponseEntity<>(Map.of("error", "Bad request"), HttpStatus.BAD_REQUEST);
        }

        String userId = customUserDetails.getUsername();

        System.out.println("userId : " + userId);

        String base64Image = null;
        try {
            base64Image = Base64.getEncoder().encodeToString(image.getBytes());
        } catch (IOException e) {
            System.out.println("Inside identifyPlant method exception inside controller");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        String[] images = new String[]{base64Image};

        System.out.println("Base64 string length: " + base64Image.length());

        if (images == null || images.length == 0)
        {
            return new ResponseEntity<>(Map.of("error", "No images found"), HttpStatus.BAD_REQUEST);
        }

        // Map<String, Object> output = healthAssessment.detectProblem(images, userId);

        /* For test the ui */
         Map<String, Object> output = testService.getPlantProblemDetails(userId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success(output.get("message").toString(),output.get("data")), HttpStatus.OK);
    }

    @GetMapping("/get-all-problem-detection-history-for-a-user/user")
    public ResponseEntity<Map<String, Object>> getAllProblemDetectionHistoryForAUser(Authentication authentication)
    {
        System.out.println("Inside getAllProblemDetectionHistoryForAUser method in controller");

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String userId = customUserDetails.getUsername();

        Map<String , Object> output = service.getDetectionHistory(userId);

        System.out.println("output : " + output);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success(output.get("message").toString(),output.get("data")), HttpStatus.OK);

    }

    @GetMapping("/get-detect-problem-history-details/documentId/{documentId}")
    public ResponseEntity<Map<String, Object>> getDetectProblemHistoryDetails(@PathVariable String documentId)
    {
        System.out.println("Inside getIdentificationHistoryDetails method in controller");
        System.out.println("identificationId : " + documentId);

        Map<String, Object> output = service.getDetailsOfPlants(documentId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success(output.get("message").toString(),output.get("data")), HttpStatus.OK);
    }

    @GetMapping("/get-response")
    public ResponseEntity<Map<String, Object>> getResponse() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
