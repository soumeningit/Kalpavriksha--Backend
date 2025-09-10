package com.soumen.kalpavriksha.Auth;

import com.soumen.kalpavriksha.Entity.RefreshToken;
import com.soumen.kalpavriksha.Entity.User;
import com.soumen.kalpavriksha.Models.AuthRequest;
import com.soumen.kalpavriksha.Repository.RefreshTokenRepository;
import com.soumen.kalpavriksha.Utills.Common;
import com.soumen.kalpavriksha.Utills.Response;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController
{
    @Autowired
    private AuthService service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRepository repository;

    @PostMapping("/register")
    public ResponseEntity<Map<String , Object>> registerController(@RequestBody AuthRequest request)
    {
        String name = request.getName();
        String email = request.getEmail();
        char[] password = request.getPassword();

        if(Common.isNullOrEmpty(name) || Common.isNullOrEmpty(email) || Common.isNullOrEmpty(password))
        {
            return ResponseEntity.badRequest().body(Response.error("Please provide all details"));
        }

        Map<String , Object> response = service.registerService(name, email, password);

        System.out.println("response after registration : " + response);


        if(!(boolean) response.get("success"))
        {
            return new ResponseEntity<>(Response.error(response.get("message")), HttpStatus.BAD_REQUEST);
        }

        String userId = response.get("data").toString();

        response = service.verificationTokenService(name, email, userId);

        return new ResponseEntity<>(Response.success("User registered successfully", response), HttpStatus.CREATED);

    }

    @PutMapping("/verify-user")
    public ResponseEntity<Map<String, Object>> verifyUser(@RequestBody AuthRequest request)
    {
        System.out.println("Inside verify user controller");

        String token = request.getVerificationToken();

        System.out.println("token : " + token);

        if(Common.isNullOrEmpty(token))
        {
            return new ResponseEntity<>(Response.error("Please provide token"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object> output = service.verifyUser(token);

        if(!(boolean)output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(Response.success(output.get("message")), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String , Object>> loginController(@RequestBody AuthRequest request, HttpServletResponse response)
    {
        System.out.println("Inside log in controller");
        String email = request.getEmail();
        char[] password = request.getPassword();

        if(Common.isNullOrEmpty(email) || Common.isNullOrEmpty(password))
        {
            return ResponseEntity.badRequest().body(Response.error("Please provide all details"));
        }

        Map<String , Object> output = service.checkUser(email, password);

        if(!(boolean)output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        System.out.println("output after check inside controller : " + output);

        int userId = (Integer) output.get("data");

        System.out.println("user id : " + userId);

        output = service.loginService(email, password, userId);

        System.out.println("output after login : " + output);

        if(!(boolean)output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object>resp = (Map<String, Object>) output.get("data");

        String refreshToken = resp.get("refreshToken").toString();

        System.out.println("refreshToken inside log in controller : " + refreshToken);

        // Set cookie with token
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth")
                .maxAge(15 * 24 * 60 * 60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        Map<String , Object> data = new HashMap<>();
        data.put("token", resp.get("token"));
        data.put("id", resp.get("id"));
        data.put("role", resp.get("role"));
        data.put("email", resp.get("email"));
        data.put("name", resp.get("name"));

        return new ResponseEntity<>(Response.success("User logged in successfully", data), HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgetPassword(@RequestBody AuthRequest request)
    {
        System.out.println("INSIDE FORGET PASSWORD CONTROLLER ....");
        String email = request.getEmail();

        System.out.println("Inside forget password controller email : " + email);

        if(Common.isNullOrEmpty(email))
        {
            return new ResponseEntity<>(Response.error("Please provide email"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object>output = service.forgetPasswordService(email);

        if(!(boolean)output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(Response.success("Email sent successfully"), HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String , Object>> resetPassword(@RequestBody AuthRequest request)
    {
        System.out.println("Inside reset password");
        String token = request.getResetPasswordToken();
        String userId = request.getUserId();
        char[] password = request.getPassword();

        System.out.println("token : " + token + " userId : " + userId + " password : " + password);

        Map<String , Object> output = service.resetPasswordService(userId, token, password);

        System.out.println("output inside reset password controller : " + output);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(Response.success("Password Changed Successfully"), HttpStatus.OK);

    }

    @PostMapping("/logout-user")
    @Transactional
    public ResponseEntity<Map<String, Object>> logOutUser(HttpServletResponse res, @CookieValue(value="refreshToken", required=false) String refreshToken)
    {
        System.out.println("INSIDE LOGOUT USER CONTROLLER ....");

        System.out.println("refreshToken inside logout controller : " + refreshToken);

        if(refreshToken != null){
            repository.deleteByToken(refreshToken);
        }

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth")
                .maxAge(0)
                .build();

        res.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());

        return new ResponseEntity<>(Response.success("User logged out successfully"), HttpStatus.OK);
    }

    @PutMapping("/update-password/userId/{userId}")
    public ResponseEntity<Map<String , Object>> updatePassword(@RequestBody AuthRequest request, @PathVariable String userId)
    {
        System.out.println("INSIDE UPDATE PASSWORD CONTROLLER ....");

        System.out.println("request : " + request);

        char[] password = request.getPassword();

        if(Common.isNullOrEmpty(userId) || Common.isNullOrEmpty(password))
        {
            return new ResponseEntity<>(Response.error("Required fields are missing"), HttpStatus.BAD_REQUEST);
        }

        Map<String , Object> output = service.updatePasswordService(userId, password);

        if(!(boolean) output.get("success"))
        {
            return new ResponseEntity<>(Response.error(output.get("message")), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(Response.success("Password updated successfully"), HttpStatus.OK);
    }

    @PostMapping("/refresh-token/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken)
    {
        System.out.println("inside refresh token controller");
        System.out.println("refreshToken : " + refreshToken);

        long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 15;

        if(refreshToken == null)
        {
            return new ResponseEntity<>(Response.error("Refresh Token is not present"), HttpStatus.NOT_FOUND);
        }

        Optional<RefreshToken> token = repository.findByToken(refreshToken);

        if(token.isEmpty())
        {
            return new ResponseEntity<>(Response.error("Refresh Token is not present"), HttpStatus.NOT_FOUND);
        }

        RefreshToken tokenEntity = token.get();

        if(tokenEntity.getExpiresAt().isBefore(LocalDateTime.now()))
        {
            return new ResponseEntity<>(Response.error("Refresh Token is expired"), HttpStatus.NOT_FOUND);
        }

        User user = tokenEntity.getUser();

        System.out.println("user : " + user);

        Payload payload = new Payload();
        payload.setEmail(user.getEmail());
        payload.setUserId(Integer.toString(user.getId()));
        payload.setRole(user.getRole().toString());

        String newAccessToken = jwtService.generateAccessToken(payload, REFRESH_TOKEN_EXPIRATION_TIME);

        Map<String , Object> data = new HashMap<>();
        data.put("token", newAccessToken);
        data.put("id", user.getId());
        data.put("role", user.getRole().toString());
        data.put("email", user.getEmail());
        data.put("name", user.getName());

        return new ResponseEntity<>(Response.success("Token refreshed successfully", data), HttpStatus.OK);

    }

    @GetMapping("/check-cookie")
    public String checkCookie(@CookieValue(value = "refreshToken", required = false) String refreshToken)
    {
        return "Cookie: " + refreshToken;
    }

}
