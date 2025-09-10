package com.soumen.kalpavriksha.Chat.ChatConfig;

import com.soumen.kalpavriksha.Auth.CustomUserDetailsService;
import com.soumen.kalpavriksha.Auth.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor
{
    @Autowired
    private JwtService jwt;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel)
    {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 1) Try Authorization: Bearer <token>
            String token = null;
            List<String> authHeaders = accessor.getNativeHeader("Authorization");
            if (authHeaders != null && !authHeaders.isEmpty()) {
                String raw = authHeaders.get(0);
                if (raw.toLowerCase().startsWith("bearer ")) {
                    token = raw.substring(7);
                }
            }
            // 2) Fallback to "token" header (if you send it that way)
            if (token == null) {
                List<String> tokens = accessor.getNativeHeader("token");
                if (tokens != null && !tokens.isEmpty()) token = tokens.get(0);
            }

            if (token == null || token.isBlank() || !jwt.isTokenValid(token)) {
                throw new IllegalArgumentException("Invalid or missing JWT in STOMP CONNECT");
            }

            // Extract userId/username from token and load UserDetails
            String userId = jwt.getUserIdFromToken(token); // adapt to your util method
            UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // Attach auth as Principal => available in @MessageMapping via Principal
            accessor.setUser((Principal) auth);
        }

        return message;
    }
}
