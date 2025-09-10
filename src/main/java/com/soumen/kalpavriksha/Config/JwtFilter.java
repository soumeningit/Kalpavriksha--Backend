package com.soumen.kalpavriksha.Config;

import com.soumen.kalpavriksha.Auth.CustomUserDetailsService;
import com.soumen.kalpavriksha.Auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Arrays;

@Component
public class JwtFilter extends OncePerRequestFilter
{
    @Autowired
    private JwtService jwt;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        System.out.println("Inside jwt filter");
        final String authHeader = request.getHeader("Authorization");
        final String token;
        final String userId;

        System.out.println("request.getRequestURI() : " + request.getRequestURI());

        if((request.getRequestURI().contains("/login") || request.getRequestURI().contains("/register") || request.getRequestURI().contains("/refresh") || request.getRequestURI().contains("/logout-user")) && request.getMethod().equals("POST"))
        {
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader == null || !authHeader.startsWith("Bearer "))
        {
            filterChain.doFilter(request, response);
            return;
        }

        token = authHeader.substring(7);

        System.out.println("Token : " + token);

        userId = jwt.getUserIdFromToken(token);

        System.out.println("User Id : " + userId);

        if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

            System.out.println("userDetails : " + userDetails);

            if(jwt.checkIsTokenValid(token, userDetails.getUsername()))
            {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        System.out.println("User Id in jwt filter : " + userId);
        System.out.println("Token is valid " + jwt.isTokenValid(token));

        filterChain.doFilter(request, response);
    }
}
