package com.soumen.kalpavriksha.Controllers;

import com.soumen.kalpavriksha.Auth.CustomUserDetails;
import com.soumen.kalpavriksha.Models.UserProfileDTO;
import com.soumen.kalpavriksha.Service.ProfileService;
import com.soumen.kalpavriksha.Utills.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController
{
    @Autowired
    private ProfileService profileService;

    @GetMapping("/get-profile-details")
    public ResponseEntity<Map<String , Object>>getProfileDetails(Authentication authentication)
    {
        System.out.println("Inside getProfileDetails method in controller");

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = customUserDetails.getUsername();

        if(userId == null)
        {
            return new ResponseEntity<>(Response.error("User id is required"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object> output = profileService.checkProfile(userId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message").toString(), output.get("data")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success("Profile details fetched successfully", output.get("data")), HttpStatus.OK);

    }

    @PutMapping("/update-profile-details")
    public ResponseEntity<Map<String , Object>> updateProfileDetails(@RequestBody UserProfileDTO userProfile, Authentication authentication)
    {
        System.out.println("Inside updateProfileDetails method in controller");

        System.out.println("user profile : " + userProfile);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = customUserDetails.getUsername();

        System.out.println("user profile : " + userProfile);

        if(userId == null)
        {
            return new ResponseEntity<>(Response.error("User id is required"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object> output = profileService.updateProfileDetails(userProfile, userId);

        System.out.println("output : " + output);

        System.out.println("output.get() : " + output.get("message"));

        System.out.println("output.get() : " + output.get("data"));

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message").toString(), output.get("data")), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(Response.success(output.get("message").toString(), output.get("data")), HttpStatus.OK);
    }

    @GetMapping("/get-user-profile-details")
    public ResponseEntity<Map<String , Object>>getUserProfileDetails(Authentication authentication)
    {
        System.out.println("Inside getProfileDetails method in controller");

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = customUserDetails.getUsername();

        if(userId == null)
        {
            return new ResponseEntity<>(Response.error("User id is required"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object> output = profileService.getUserProfile(userId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message").toString(), output.get("data")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success("Profile details fetched successfully", output.get("data")), HttpStatus.OK);

    }

    @GetMapping("/get-history")
    public ResponseEntity<Map<String , Object>>getHistory(Authentication authentication)
    {
        System.out.println("Inside getHistory method in controller");

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String userId = customUserDetails.getUsername();

        if(userId == null)
        {
            return new ResponseEntity<>(Response.error("User id is required"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object> output = profileService.getHistory(userId);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message").toString(), output.get("data")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success("History fetched successfully", output.get("data")), HttpStatus.OK);

    }

}
