package com.soumen.kalpavriksha.Auth;

import com.soumen.kalpavriksha.Entity.User;
import com.soumen.kalpavriksha.Models.UserDTO;
import com.soumen.kalpavriksha.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AuthUserRepo
{
    private final UserRepository userRepository;
    public UserDTO findUserByUserId(String userId) // here the user is the custom user details
    {
        Optional<User> userOptional = userRepository.findById(Integer.parseInt(userId));

        if(userOptional.isEmpty())
        {
            return null;
        }
        User user = userOptional.get();

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().toString());
        userDTO.setUserId(Integer.toString(user.getId()));
        userDTO.setVerified(user.isVerified());
        userDTO.setPassword(user.getPassword());

        return userDTO;

    }
}


