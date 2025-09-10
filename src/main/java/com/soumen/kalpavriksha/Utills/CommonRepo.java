package com.soumen.kalpavriksha.Utills;

import com.soumen.kalpavriksha.Entity.User;
import com.soumen.kalpavriksha.Models.UserDTO;
import com.soumen.kalpavriksha.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommonRepo
{
    private final UserRepository userRepo;

    public boolean isUserExists(String email)
    {
        return userRepo.existsByEmail(email);
    }

    public UserDTO findUserByEmail(String email)
    {
        User user = userRepo.findByEmail(email).orElse(null);

        if(user == null)
        {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().toString());
        userDTO.setUserId(Integer.toString(user.getId()));
        userDTO.setVerified(user.isVerified());

        return userDTO;
    }
}
