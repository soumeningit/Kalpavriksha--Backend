package com.soumen.kalpavriksha.Utills;

import java.util.Map;

public class Response
{
    private static Map<String, Object> successResponse(String message, Object data)
    {
        return Map.of("success", true, "message", message, "data", data);
    }
    private static Map<String, Object> successResponse(String message)
    {
        return Map.of("success", true, "message", message);
    }
    private static Map<String, Object> successResponse( Object data)
    {
        return Map.of("success", true, "data", data);
    }

    private static Map<String, Object> errorResponse(String message, Object data)
    {
        return Map.of("success", false, "message", message, "data", data);
    }
    private static Map<String, Object> errorResponse(String message)
    {
        return Map.of("success", false, "message", message);
    }
    private static Map<String, Object> errorResponse( Object data)
    {
        return Map.of("success", false, "data", data);
    }

    public static Map<String , Object> success(String message, Object data)
    {
        return successResponse(message, data);
    }
    public static Map<String , Object> success(String message)
    {
        return successResponse(message);
    }
    public static Map<String , Object> success(Object data)
    {
        return successResponse(data);
    }

    public static Map<String , Object> error(String message, Object data)
    {
        return errorResponse(message, data);
    }
    public static Map<String , Object> error(String message)
    {
        return errorResponse(message);
    }
    public static Map<String , Object> error(Object data)
    {
        return errorResponse(data);
    }

}
