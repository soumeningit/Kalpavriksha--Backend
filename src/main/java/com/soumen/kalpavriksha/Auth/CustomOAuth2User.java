package com.soumen.kalpavriksha.Auth;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

/*
    As of now it is not required as i manage OAuth2(Sign In With Google) manually, not add Spring Security
 */
@Deprecated
public class CustomOAuth2User extends DefaultOAuth2User
{
    private final String userId;
    private final String role;
    private final boolean isVerified;


    public CustomOAuth2User(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes,
            String nameAttributeKey,
            String userId,
            String role,
            boolean isVerified
    )
    {
        super(authorities, attributes, nameAttributeKey);
        this.userId = userId;
        this.role = role;
        this.isVerified = isVerified;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getRole()
    {
        return role;
    }

    public boolean isVerified()
    {
        return isVerified;
    }
}
