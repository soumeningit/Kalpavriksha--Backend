package com.soumen.kalpavriksha.Service;

import com.soumen.kalpavriksha.Entity.Address;
import com.soumen.kalpavriksha.Entity.Gender;
import com.soumen.kalpavriksha.Entity.User;
import com.soumen.kalpavriksha.Entity.UserProfile;
import com.soumen.kalpavriksha.Models.UserDTO;
import com.soumen.kalpavriksha.Models.UserProfileDTO;
import com.soumen.kalpavriksha.Repository.UserProfileRepository;
import com.soumen.kalpavriksha.Repository.UserRepository;
import com.soumen.kalpavriksha.Utills.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProfileService
{
    private final UserRepository userRepository;

    private final UserProfileRepository userProfileRepository;

    public Map<String , Object> checkProfile(String userId)
    {
        Optional<User> userOptional = userRepository.findById(Integer.parseInt(userId));

        if(userOptional.isEmpty())
        {
            return null;
        }

        User user = userOptional.get();

        System.out.println("user : " + user);

        UserProfile userProfile = user.getProfile();

        System.out.println("userProfile : " + userProfile);

        if(userProfile == null)
        {
            return Response.error("Profile not found", 000);
        }

        return Response.success("Profile found", userProfile);

    }

    public Map<String, Object> updateProfileDetails(UserProfileDTO userProfileDTO, String userId) {
        try {
            // Fetch user
            Optional<User> userOptional = userRepository.findById(Integer.parseInt(userId));
            if (userOptional.isEmpty()) {
                return Response.error("User not found");
            }
            User user = userOptional.get();
            System.out.println("user : " + user);

            // Check if the user already has a profile
            UserProfile profile = user.getProfile();
            if (profile == null) {
                profile = new UserProfile();
                profile.setUser(user);
            }

            // Map DTO to Address
            Address address = new Address();
            address.setLandmark(userProfileDTO.getLandmark());
            address.setStreet(userProfileDTO.getStreet());
            address.setCity(userProfileDTO.getCity());
            address.setState(userProfileDTO.getState());
            address.setCountry(userProfileDTO.getCountry());
            address.setPostalCode(userProfileDTO.getPostalCode());

            // Set address and other fields
            profile.setAddress(address);
            profile.setBio(userProfileDTO.getBio());
            profile.setDob(userProfileDTO.getDob());
            Gender gender = Gender.valueOf(userProfileDTO.getGender().toString().toUpperCase().trim());
            profile.setGender(gender);

            System.out.println("profile : " + profile);

            // Save the profile
            UserProfile savedProfile = userProfileRepository.save(profile);
            System.out.println("response : " + savedProfile);

            UserDTO userData = new UserDTO();
            userData.setId(user.getId());
            userData.setName(user.getName());
            userData.setEmail(user.getEmail());
            userData.setRole(user.getRole().toString());
            userData.setUserId(Integer.toString(user.getId()));
            userData.setVerified(user.isVerified());
            userData.setImageUrl(user.getImageUrl());
            userData.setLandmark(userProfileDTO.getLandmark());
            userData.setStreet(userProfileDTO.getStreet());
            userData.setCity(userProfileDTO.getCity());
            userData.setState(userProfileDTO.getState());
            userData.setCountry(userProfileDTO.getCountry());
            userData.setPostalCode(userProfileDTO.getPostalCode());
            userData.setBio(userProfileDTO.getBio());
            userData.setDob(userProfileDTO.getDob());
            userData.setGender(userProfileDTO.getGender().toString());

            return Response.success("Profile updated successfully", userData);

        } catch (Exception e) {
            e.printStackTrace();
            String errorMessage = (e.getMessage() == null || e.getMessage().isBlank())
                    ? "Unexpected error occurred"
                    : e.getMessage();
            return Response.error(errorMessage);
        }
    }

    public Map<String , Object> getUserProfile(String userId)
    {
        Optional<User> userOptional = userRepository.findById(Integer.parseInt(userId));

        if(userOptional.isEmpty())
        {
            return null;
        }

        User user = userOptional.get();

        System.out.println("user : " + user);

        if(user == null)
        {
            return Response.error("Profile not found", 000);
        }

        UserProfile userProfile = user.getProfile();

        System.out.println("userProfile : " + userProfile);

        if(userProfile == null)
        {
            return Response.error("Profile not found", 000);
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().toString());
        userDTO.setUserId(Integer.toString(user.getId()));
        userDTO.setVerified(user.isVerified());
        userDTO.setImageUrl(user.getImageUrl());
        userDTO.setLandmark(userProfile.getAddress().getLandmark());
        userDTO.setStreet(userProfile.getAddress().getStreet());
        userDTO.setCity(userProfile.getAddress().getCity());
        userDTO.setState(userProfile.getAddress().getState());
        userDTO.setCountry(userProfile.getAddress().getCountry());
        userDTO.setPostalCode(userProfile.getAddress().getPostalCode());
//        userDTO.setLatitude(userProfile.getAddress().getLatitude());
//        userDTO.setLongitude(userProfile.getAddress().getLongitude());
        userDTO.setBio(userProfile.getBio());
        userDTO.setDob(userProfile.getDob());
        userDTO.setGender(userProfile.getGender()==null?"":userProfile.getGender().toString());

        return Response.success("Profile found", userDTO);

    }

    public Map<String, Object> getHistory(String userId)
    {
        try {
            // Fetch user
            Optional<User> userOptional = userRepository.findById(Integer.parseInt(userId));
            if (userOptional.isEmpty()) {
                return Response.error("User not found");
            }
            User user = userOptional.get();
            System.out.println("user : " + user);



            return Response.success("Profile updated successfully");

        } catch (Exception e) {
            String errorMessage = (e.getMessage() == null || e.getMessage().isBlank())
                    ? "Unexpected error occurred"
                    : e.getMessage();
            return Response.error(errorMessage);
        }
    }
}
