package com.soumen.kalpavriksha.Service;

import com.soumen.kalpavriksha.Auth.JwtService;
import com.soumen.kalpavriksha.Auth.Payload;
import com.soumen.kalpavriksha.Entity.OAuth2User;
import com.soumen.kalpavriksha.Entity.RefreshToken;
import com.soumen.kalpavriksha.Entity.User;
import com.soumen.kalpavriksha.Models.GoogleUser;
import com.soumen.kalpavriksha.Repository.OAuth2UserRepository;
import com.soumen.kalpavriksha.Repository.RefreshTokenRepository;
import com.soumen.kalpavriksha.Repository.UserRepository;
import com.soumen.kalpavriksha.Utills.Response;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OAuth2Service
{
    @Value("${google.client-id}")
    private String clientId;

    @Value("${google.client-secret}")
    private String clientSecret;

    @Value("${google.redirect-uri}")
    private String redirectUri;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private OAuth2UserRepository oAuth2UserRepo;

    @Autowired
    private RefreshTokenRepository refreshTokenRepo;

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String , Object> getAccessToken(String code)
    {
        System.out.println("Inside getAccessToken method inside OAuth2Service");

        String tokenUrl = "https://oauth2.googleapis.com/token";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        Map<String, Object> response = restTemplate.postForObject(tokenUrl, params, Map.class);

        System.out.println("response inside OAuth2Service : " + response);

        if(response.containsKey("access_token"))
        {
            return Response.success("access_token", response.get("access_token"));
        }

        return Response.error("error", response);
    }

    public GoogleUser getUserInfo(String accessToken)
    {
        String url = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<GoogleUser> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, GoogleUser.class);

        return response.getBody();
    }

    @Transactional
    public Map<String , Object> checkUser(String name, String email, String idFromGoogle, String profileURL)
    {
        try{
            Optional<User> optionalUser = userRepository.findByEmail(email);

            User user = new User();
            OAuth2User oAuth2User = new OAuth2User();

            if(optionalUser.isPresent() && optionalUser.get().isOauthUser())
            {
                System.out.println("User already exists");
                return Response.success("User found", optionalUser.get());
            }

            if(optionalUser.isPresent())
            {
                System.out.println("User already exists but not oauth user");
                user = optionalUser.get();
                user.setPassword(null);
                user.setOauthUser(true);
                user.setVerified(true);

                oAuth2User.setUser(user);
                oAuth2User.setClientName("Google");
                oAuth2User.setRegistrationId(idFromGoogle);
                oAuth2User.setCreatedAt(LocalDateTime.now());
                User savedUser = userRepository.save(user);
                oAuth2UserRepo.save(oAuth2User);

                return Response.success("User found", savedUser);
            }

            System.out.println("User not found");
            System.out.println("Creating user");
            user.setEmail(email);
            user.setName(name);
            user.setOauthUser(true);
            user.setVerified(true);
            user.setImageUrl(profileURL);

            User savedUser = userRepository.save(user);

            oAuth2User.setUser(savedUser);
            oAuth2User.setClientName("Google");
            oAuth2User.setRegistrationId(idFromGoogle);
            oAuth2User.setCreatedAt(LocalDateTime.now());

            oAuth2UserRepo.save(oAuth2User);

            return Response.success("User found", savedUser);
        } catch (Exception e) {
            return Response.error("User not found");
        }

    }

    public Map<String , Object> oAuth2LoginUser(User user)
    {
        try{
            String userEmail = user.getEmail();
            int id = user.getId();
            String role = user.getRole().toString();
            String userId = Integer.toString(id);
            String userName = user.getName();

            System.out.println("userEmail inside OAuth2Controller : " + userEmail);
            System.out.println("id inside OAuth2Controller : " + id);
            System.out.println("role inside OAuth2Controller : " + role);
            System.out.println("userId inside OAuth2Controller : " + userId);
            System.out.println("userName inside OAuth2Controller : " + userName);

            Payload payload = new Payload();
            payload.setUserId(String.valueOf(id));
            payload.setRole(role);
            payload.setEmail(userEmail);

            long EXPIRATION_TIME = 1000 * 60 * 60 * 2;
            long REFRESH_TOKEN_EXPIRATION_TIME = 15 * 24 * 60 * 60 * 1000;

            String token = new JwtService().generateAccessToken(payload, EXPIRATION_TIME);

            System.out.println("generated token : " + token);

            String refreshToken = new JwtService().createRefreshToken(payload, REFRESH_TOKEN_EXPIRATION_TIME);

            System.out.println("refresh token : " + refreshToken);

            Map<String , Object> tokenData = new HashMap<>();

            tokenData.put("token", token);
            tokenData.put("refreshToken", refreshToken);
            tokenData.put("userId", Integer.toString(user.getId()));
            tokenData.put("name", user.getName());
            tokenData.put("email", user.getEmail());

            Optional<RefreshToken> refreshTokenOptional= refreshTokenRepo.findByUser(user);

            if(refreshTokenOptional.isPresent())
            {
                RefreshToken object = refreshTokenOptional.get();
                object.setToken(refreshToken);
                object.setExpiresAt(LocalDateTime.now().plusDays(15));
                refreshTokenRepo.save(object);

                return Response.success("Success", tokenData);
            }

            RefreshToken refreshTokenObject = new RefreshToken();

            refreshTokenObject.setToken(refreshToken);
            refreshTokenObject.setUser(user);
            refreshTokenObject.setCreatedAt(LocalDateTime.now());
            refreshTokenObject.setExpiresAt(LocalDateTime.now().plusDays(15));

            refreshTokenRepo.save(refreshTokenObject);

            return Response.success("Success", tokenData);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }
    }

}
