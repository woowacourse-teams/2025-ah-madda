package com.ahmadda.domain.util;

import com.ahmadda.domain.exception.NullPropertyException;

public class Assert {

    public static <T> T notNull(T obj, String message) {
        if (obj == null) {
            throw new NullPropertyException(message);
        }
        return obj;
    }
}
