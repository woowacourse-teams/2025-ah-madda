package com.ahmadda.domain.util;

import com.ahmadda.domain.exception.BlankPropertyException;
import com.ahmadda.domain.exception.NullPropertyException;

public class Assert {

    public static void notNull(final Object obj, final String message) {
        if (obj == null) {
            throw new NullPropertyException(message);
        }
    }

    public static void notBlank(final String obj, final String message) {
        if (obj == null) {
            throw new NullPropertyException(message);
        }

        if (obj.isBlank()) {
            throw new BlankPropertyException(message);
        }
    }
}
