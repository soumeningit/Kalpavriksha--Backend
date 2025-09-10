package com.soumen.kalpavriksha.Utills;

import java.security.SecureRandom;
import java.util.UUID;

public class AuthUtil
{
    private static final String literal = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    private static String generateToken()
    {
        return generateToken(15);
    }
    private static String generateToken(int length)
    {
        StringBuilder token = new StringBuilder();

        for(int i = 0 ; i < length; i++)
        {
            int index = random.nextInt(length);
            token.append(literal.charAt(index));
        }
        token.append("-").append(UUID.randomUUID());

        return token.toString();
    }

    public static String getRegisterToken()
    {
        return generateToken();
    }

    public static String getResetPasswordToken(int length)
    {
        return generateToken(length);
    }
}
