package com.soumen.kalpavriksha.Controllers;

import com.soumen.kalpavriksha.Auth.JwtService;
import com.soumen.kalpavriksha.Auth.Payload;
import com.soumen.kalpavriksha.Entity.RefreshToken;
import com.soumen.kalpavriksha.Entity.User;
import com.soumen.kalpavriksha.Models.GoogleUser;
import com.soumen.kalpavriksha.Repository.RefreshTokenRepository;
import com.soumen.kalpavriksha.Service.OAuth2Service;
import com.soumen.kalpavriksha.Utills.Response;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class OAuthController
{
    @Value("${frontend.url}")
    private String clientUrl;

    @Autowired
    private OAuth2Service service;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepo;

    @GetMapping("/google/callback")
    public ResponseEntity<?> googleCallback(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String error, HttpServletResponse httpServletResponse) {

        if (error != null) {
            return new ResponseEntity<>(Response.error("Google login failed: " + error), HttpStatus.BAD_REQUEST);
        }

        if (code == null) {
            return new ResponseEntity<>(Response.error("Missing authorization code"), HttpStatus.BAD_REQUEST);
        }

        // Exchange code for tokens
        Map<String, Object> response = service.getAccessToken(code);

        System.out.println("response inside OAuth2Controller : " + response);

        String accessToken = (String) response.get("data");

        // Fetch user info from Google
        GoogleUser user = service.getUserInfo(accessToken);

        System.out.println("user inside OAuth2Controller : " + user);

        String email = user.getEmail();
        String name = user.getName();
        String picture = user.getPicture();
        String idFromGoogle = user.getSub();

        System.out.println("email inside OAuth2Controller : " + email);
        System.out.println("name inside OAuth2Controller : " + name);
        System.out.println("picture inside OAuth2Controller : " + picture);
        System.out.println("idFromGoogle inside OAuth2Controller : " + idFromGoogle);

        // Handle signup/login logic in your app
        // String jwt = jwtService.generateToken(user);
        /* all the jwt token logic will be here */

        response = service.checkUser(name,email,idFromGoogle, picture);
        System.out.println("response inside OAuth2Controller : " + response);

        if(!(boolean) response.get("success"))
        {
            return new ResponseEntity<>(Response.error(response.get("message")), HttpStatus.BAD_REQUEST);
        }

        User userFromDB = (User) response.get("data");

        System.out.println("userFromDB inside OAuth2Controller : " + userFromDB);

        String userEmail = userFromDB.getEmail();
        int id = userFromDB.getId();
        String role = userFromDB.getRole().toString();
        String userId = Integer.toString(id);
        String userName = userFromDB.getName();

        System.out.println("userEmail inside OAuth2Controller : " + userEmail);
        System.out.println("id inside OAuth2Controller : " + id);
        System.out.println("role inside OAuth2Controller : " + role);
        System.out.println("userId inside OAuth2Controller : " + userId);
        System.out.println("userName inside OAuth2Controller : " + userName);

        Payload payload = new Payload();
        payload.setUserId(String.valueOf(id));
        payload.setRole(role);
        payload.setEmail(userEmail);

        long EXPIRATION_TIME = 1000 * 60 * 2;
        long REFRESH_TOKEN_EXPIRATION_TIME = 15 * 24 * 60 * 60 * 1000;

        String token = new JwtService().generateAccessToken(payload, EXPIRATION_TIME);

        System.out.println("generated token : " + token);

        String refreshToken = new JwtService().createRefreshToken(payload, REFRESH_TOKEN_EXPIRATION_TIME);

        System.out.println("refresh token : " + refreshToken);

        RefreshToken refreshTokenObject = new RefreshToken();

        refreshTokenObject.setToken(refreshToken);
        refreshTokenObject.setUser(userFromDB);
        refreshTokenObject.setCreatedAt(LocalDateTime.now());
        refreshTokenObject.setExpiresAt(LocalDateTime.now().plusDays(15));

        refreshTokenRepo.save(refreshTokenObject);

        // Set cookie with token
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true) // set true in production (HTTPS)
                .path("/api/v1/auth")
                .maxAge(15 * 24 * 60 * 60)
                .sameSite("None")
                .build();

        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        // Redirect back to frontend with token
        String frontendUrl = clientUrl + "/user/login?token=" + token + "&userId=" + userId + "&userName=" + userName;
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", frontendUrl)
                .build();
    }

}
