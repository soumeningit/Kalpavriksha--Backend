package com.soumen.kalpavriksha.Auth;

import com.soumen.kalpavriksha.Models.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
    @Autowired
    private AuthUserRepo userRepository;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException
    {
        System.out.println("Inside loadUserByUsername method userId : " + userId);
        UserDTO user = userRepository.findUserByUserId(userId);

        System.out.println("user inside loadUserByUsername method : " + user);

        if(user == null)
        {
            throw new UsernameNotFoundException("User not found");
        }

        return new CustomUserDetails(
                user.getUserId(),
                user.getPassword(),
                user.getRole(),
                user.isVerified()
        );

    }


}
