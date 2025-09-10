package com.soumen.kalpavriksha.Chat.ChatConfig;

import com.soumen.kalpavriksha.Auth.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor
{
    @Autowired
    private JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servlet) {
            String token = servlet.getServletRequest().getParameter("token"); // sent as query param
            if (token == null || token.isBlank()) {
                return false;
            }

            System.out.println("token before handshake: " + token);

            try {
                if (!jwtService.isTokenValid(token)) {
                    return false;
                }
                // Use whatever your util exposes (userId/username)
                String userId = jwtService.getUserIdFromToken(token); // or extractUsername(...)
                attributes.put("userId", userId);

                System.out.println("userId before handshake: " + userId);

                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception)
    {
        System.out.println("after handshake");
    }

}
