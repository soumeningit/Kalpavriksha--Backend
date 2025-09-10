package com.soumen.kalpavriksha.Auth;

import com.soumen.kalpavriksha.Entity.RefreshToken;
import com.soumen.kalpavriksha.Entity.User;
import com.soumen.kalpavriksha.Entity.UserToken;
import com.soumen.kalpavriksha.Models.UserDTO;
import com.soumen.kalpavriksha.Repository.OAuth2UserRepository;
import com.soumen.kalpavriksha.Repository.RefreshTokenRepository;
import com.soumen.kalpavriksha.Repository.UserRepository;
import com.soumen.kalpavriksha.Repository.UserTokenRepository;
import com.soumen.kalpavriksha.Utills.AuthUtil;
import com.soumen.kalpavriksha.Utills.CommonRepo;
import com.soumen.kalpavriksha.Utills.Response;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService
{
    @Autowired
    private CommonRepo cRepo;

    @Autowired
    private EmailService emailService;

    @Value("${frontend.url}")
    private String clientUrl;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private RefreshTokenRepository refreshTokenRepo;

    private final UserRepository userRepo;
    private final UserTokenRepository userTokenRepo;
    private final OAuth2UserRepository oAuth2UserRepo;

    public Map<String, Object> registerService(String name, String email, char[] password)
    {
        try{
            Optional<User> userOptional = userRepo.findByEmail(email);

            if(userOptional.isPresent())
            {
                return Response.error("User already exists");
            }

            String rawPassword = new String(password);

            System.out.println("rawPassword : " + rawPassword);

            String hashedPassword = new BCryptPasswordEncoder().encode(rawPassword);

            Arrays.fill(password, '0');

            rawPassword = null;

            System.out.println("hashedPassword : " + hashedPassword);

            User user = User.builder()
                    .name(name)
                    .email(email)
                    .password(hashedPassword)
                    .build();

            System.out.println("user : " + user);

            User resp = userRepo.save(user);

            System.out.println("resp : " + resp);

            System.out.println("user id : " + resp.getId());
            System.out.println("userId : " + resp.getUserId());

            return Response.success("User registered successfully", resp.getId());

        } catch (Exception e) {
            e.printStackTrace();
            return Response.error("Internal server error" + e.getMessage());
        }
    }

    @Transactional
    public Map<String , Object> verificationTokenService(String name, String email, String userId)
    {
        Optional<User>userOptional = userRepo.findByEmail(email);

        if(userOptional.isEmpty())
        {
            return Response.error("User not found");
        }

        User user = userOptional.get();

        if(user.isVerified())
        {
            return Response.error("User already verified");
        }

        String token = AuthUtil.getRegisterToken();

        System.out.println("created token : " + token);

        if(token.isEmpty() || token == null)
        {
            return Response.error("Internal server error: Token not created");
        }

        UserToken userToken = UserToken.builder()
                .registrationToken(token)
                .user(user)
                .registerTokenCreatedAt(LocalDateTime.now())
                .registerTokenExpiresAt(LocalDateTime.now().plusMinutes(15))
                .build();

        try{
            userTokenRepo.save(userToken);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error("Internal server error");
        }

        System.out.println("userToken : " + userToken);

        String URL = clientUrl + "/register/verify/" + token + "/uid/"+userId;

         String logoURL = "https://res.cloudinary.com/dhu8fpog1/image/upload/v1756536416/Kalpavriksha_Logo_okzjcn.png";
         String expiryMinutes = "15";
         String supportEmail = "support@kalpavriksha.com";

        Map<String , Object> response = emailService.sendUserVerifyMail(email, name, token, logoURL, URL, expiryMinutes, supportEmail);

        if(!(boolean) response.get("success"))
        {
            return Response.error(response.get("message"));
        }

        return Response.success(response.get("message"));
    }

    @Transactional
    @Modifying
    public Map<String, Object> verifyUser(String token)
    {
        List<UserToken> userTokens = userTokenRepo.findValidTokens(token, PageRequest.of(0, 1));

        if(userTokens.isEmpty())
        {
            return Response.error("Invalid token");
        }

        UserToken userToken = userTokens.get(0);

        System.out.println("Token from database : " + userToken.getRegistrationToken());

        User user = userToken.getUser();

        System.out.println("user : " + user);

        StringBuilder userHandle = new StringBuilder();

        String name = user.getName().trim();
        String subStr = "";

        for(int i = 0; i < name.length(); i++)
        {
            if(name.charAt(i) == ' ')
            {
                subStr = name.substring(0, i);
                break;
            }
        }

        String randomId = UUID.randomUUID().toString();

        userHandle.append("@");
        userHandle.append(subStr);
        userHandle.append("_");
        userHandle.append(randomId);

        user.setUserHandle(userHandle.toString());

        System.out.println("userHandle : " + userHandle);

        subStr = null;

        user.setUserId(randomId);

        if(user.isVerified())
        {
            return Response.error("User already verified");
        }

        user.setVerified(true);

        /*
            if here i don't use save method here then also jpa will save the data in user table
            because i use @Transactional annotation here so it is in the same Persistence Context
            so when i change the user in same Persistence Context then jpa treat it as dirty read
            and it will save the data in user table before commiting the transaction
         */

        try{
            userRepo.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error("Internal server error");
        }

        return Response.success("User verified successfully", user.getId());

    }

    public Map<String, Object> checkUser(String email, char[] password)
    {
        Optional<User> userOptional = userRepo.findByEmail(email);

        if(userOptional.isEmpty())
        {
            return Response.error("User not found");
        }

        User user = userOptional.get();

        boolean isUserVerified = user.isVerified();

        if(!isUserVerified)
        {
            return Response.error("User not verified");
        }

        int userId = user.getId();

        boolean isUserRegisteredWithOAuth2 = oAuth2UserRepo.existsByUser(user);

        if(isUserRegisteredWithOAuth2)
        {
            return Response.error("User registered with OAuth2");
        }

        return Response.success("User found", userId);
    }

    public Map<String, Object> loginService(String email, char[] password, int userId)
    {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(userOptional.isEmpty())
        {
            return Response.error("User not found");
        }

        User user = userOptional.get();

        System.out.println("user dto inside login service : " + user);

        String rawPassword = new String(password);

        System.out.println("rawPassword : " + rawPassword);

        try{
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userId, rawPassword)
            );

            System.out.println("authentication : " + authentication);

            System.out.println("authentication.isAuthenticated() : " + authentication.isAuthenticated());

            System.out.println("authentication.getPrincipal() : " + authentication.getPrincipal());

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            System.out.println("userDetails : " + userDetails);
            System.out.println("userDetails.getName() : " + userDetails.getPassword());

            if (authentication.isAuthenticated()) {

                String role = user.getRole().toString();
                String userEmail = user.getEmail();
                String userIdStr = String.valueOf(user.getId());

                Payload payload = new Payload();
                payload.setEmail(userEmail);
                payload.setUserId(userIdStr);
                payload.setRole(role);

                System.out.println("payload : " + payload);

                long EXPIRATION_TIME = 1000 * 60 * 60;
                long REFRESH_TOKEN_EXPIRATION_TIME = 15 * 24 * 60 * 60 * 1000;

                String token = new JwtService().generateAccessToken(payload, EXPIRATION_TIME);

                System.out.println("generated token : " + token);

                String refreshToken = new JwtService().createRefreshToken(payload, REFRESH_TOKEN_EXPIRATION_TIME);

                System.out.println("refresh token : " + refreshToken);

                Optional<RefreshToken> refreshTokenOptional = refreshTokenRepo.findByUser(user);

                if(refreshTokenOptional.isPresent())
                {
                    RefreshToken refreshTokenData = refreshTokenOptional.get();
                    refreshTokenData.setToken(refreshToken);
                    refreshTokenData.setCreatedAt(LocalDateTime.now());
                    refreshTokenData.setExpiresAt(LocalDateTime.now().plusDays(15));

                    refreshTokenRepo.save(refreshTokenData);
                }


                RefreshToken refreshTokenObject = new RefreshToken();

                refreshTokenObject.setToken(refreshToken);
                refreshTokenObject.setUser(user);
                refreshTokenObject.setCreatedAt(LocalDateTime.now());
                refreshTokenObject.setExpiresAt(LocalDateTime.now().plusDays(15));

                refreshTokenRepo.save(refreshTokenObject);

                Map<String, Object> data = new HashMap<>();
                data.put("refreshToken", refreshToken);
                data.put("token", token);
                data.put("id", userId);
                data.put("role", role);
                data.put("email", userEmail);
                data.put("name", user.getName());

                rawPassword = null;

                System.out.println("data in log in service : " + data);

                return Response.success("User logged in successfully", data);

            }

            return Response.error("Login failed");

        } catch (AuthenticationException e) {
            System.out.println("Inside authentication exception : ");
            e.printStackTrace();
            return Response.error(e.getMessage());
        }
    }

    @Transactional
    public Map<String, Object> forgetPasswordService(String email)
    {
        Optional<User> userOptional = userRepo.findByEmail(email);

        if(userOptional.isEmpty())
        {
            return Response.error("User not found");
        }

        User user = userOptional.get();

        String name = user.getName();
        int userId = user.getId();

        String resetToken = AuthUtil.getResetPasswordToken(15);
        System.out.println("Reset Token : " + resetToken);

        LocalDateTime createdAt = LocalDateTime.now();

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

        UserToken userToken = userTokenRepo.findByUser(user)
                .orElse(UserToken.builder().user(user).build());

        userToken.setResetToken(resetToken);
        userToken.setResetTokenCreatedAt(createdAt);
        userToken.setResetTokenExpiresAt(expiresAt);

        userTokenRepo.save(userToken);


        String logoURL = "https://res.cloudinary.com/dhu8fpog1/image/upload/v1756536416/Kalpavriksha_Logo_okzjcn.png";
        String expiryMinutes = "15";
        String supportEmail = "support@kalpavriksha.com";
        String url = clientUrl + "/update-password?token="+ resetToken + "&uI=" + userId;
        String year = "2025";

        Map<String, Object> output = emailService.sendResetPasswordEmail(email, name, logoURL, url, expiryMinutes, supportEmail, year);

        if(!(boolean)output.get("success"))
        {
            return Response.error(output.get("message"));
        }

        return Response.success("Email sent successfully");
    }

    public Map<String, Object> resetPasswordService(String userId, String token, char[] password)
    {
        String rawPassword = new String(password);
        String hashPassword = new BCryptPasswordEncoder().encode(rawPassword);

        Arrays.fill(password, '0');

        rawPassword = null;
        System.out.println("hashed password : " + hashPassword);

        List<UserToken> userTokens = userTokenRepo.findUserByToken(token, PageRequest.of(0, 1));

        if(userTokens.isEmpty())
        {
            return Response.error("Invalid token");
        }

        UserToken userToken = userTokens.get(0);

        System.out.println("output in update password : " + userToken);

        String tokenFromDb = userToken.getResetToken();

        System.out.println("token from db : " + tokenFromDb);

        if(!token.equals(tokenFromDb))
        {
            return Response.error("Token Mismatch");
        }

        User user = userToken.getUser();

        System.out.println("user in reset password service : " + user);

        user.setPassword(hashPassword);

        try {
            userRepo.save(user);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }

        return Response.success("Password Changed Successfully");
    }

    public Map<String, Object> updatePasswordService(String userId, char[] password)
    {
        Optional<User> userOptional = userRepo.findById(Integer.parseInt(userId));

        if(userOptional.isEmpty())
        {
            return Response.error("User not found");
        }

        User user = userOptional.get();

        String passwordString = new String(password);
        String hashPassword = new BCryptPasswordEncoder().encode(passwordString);

        Arrays.fill(password, '0');
        passwordString = null;

        if(hashPassword.isEmpty())
        {
            return Response.error("Internal server error");
        }

        user.setPassword(hashPassword);

        try {
            userRepo.save(user);
        } catch (Exception e) {
            return Response.error(e.getMessage());
        }

        return Response.success("Password Changed Successfully");
    }
}
