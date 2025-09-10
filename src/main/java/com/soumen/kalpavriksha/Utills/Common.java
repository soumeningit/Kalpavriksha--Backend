package com.soumen.kalpavriksha.Utills;

import java.util.Collection;
import java.util.Map;

public class Common 
{
    private static boolean isNullOrEmptyCheck(Object data)
    {
        if (data == null) {
            return true;
        }

        // For String
        if (data instanceof String) {
            return ((String) data).trim().isEmpty();
        }

        // For Map
        if (data instanceof Map<?, ?>) {
            return ((Map<?, ?>) data).isEmpty();
        }

        // For Collection (List, Set, etc.)
        if (data instanceof Collection<?>) {
            return ((Collection<?>) data).isEmpty();
        }

        // For Array
        if (data.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(data) == 0;
        }

        // For Numbers (treat 0 or 0.0 as empty)
        if (data instanceof Number) {
            return ((Number) data).doubleValue() == 0.0;
        }

        return false;
    }

    public static boolean isNullOrEmpty(Object data)
    {
        return isNullOrEmptyCheck(data);
    }

}
