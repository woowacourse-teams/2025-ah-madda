package com.ahmadda.domain.util;

import com.ahmadda.domain.exception.BlankPropertyException;
import com.ahmadda.domain.exception.NullPropertyException;

public class Assert {

    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new NullPropertyException(message);
        }
    }

    public static void notBlank(String obj, String message) {
        if (obj == null) {
            throw new NullPropertyException(message);
        }

        if (obj.isBlank()) {
            throw new BlankPropertyException(message);
        }
    }
}
