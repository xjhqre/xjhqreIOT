package com.xjhqre.common.utils;

import java.util.Collection;
import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.xjhqre.common.exception.ServiceException;

public abstract class AssertUtils {
    public AssertUtils() {}

    public static void isFalse(boolean expression, String message) {
        if (expression) {
            throw new ServiceException(message);
        }
    }

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new ServiceException(message);
        }
    }

    public static void isNull(@Nullable Object object, String message) {
        if (object != null) {
            throw new ServiceException(message);
        }
    }

    public static void notNull(@Nullable Object object, String message) {
        if (object == null) {
            throw new ServiceException(message);
        }
    }

    public static void doesNotContain(@Nullable String textToSearch, String substring, String message) {
        if (StringUtils.isNotEmpty(textToSearch) && StringUtils.isNotEmpty(substring)
            && textToSearch.contains(substring)) {
            throw new ServiceException(message);
        }
    }

    public static void noNullElements(@Nullable Object[] array, String message) {
        if (array != null) {
            for (Object element : array) {
                if (element == null) {
                    throw new ServiceException(message);
                }
            }
        }

    }

    public static void notEmpty(@Nullable String text, String message) {
        if (StringUtils.isBlank(text)) {
            throw new ServiceException(message);
        }
    }

    public static void notEmpty(@Nullable Object[] array, String message) {
        if (ObjectUtils.isEmpty(array)) {
            throw new ServiceException(message);
        }
    }

    public static void notEmpty(@Nullable Collection<?> collection, String message) {
        if (CollectionUtils.isEmpty(collection)) {
            throw new ServiceException(message);
        }
    }

    public static void notEmpty(@Nullable Map<?, ?> map, String message) {
        if (CollectionUtils.isEmpty(map)) {
            throw new ServiceException(message);
        }
    }

    public static void isEmpty(@Nullable String text, String message) {
        if (!StringUtils.isNotEmpty(text)) {
            throw new ServiceException(message);
        }
    }

    public static void isEmpty(@Nullable Object[] array, String message) {
        if (!ObjectUtils.isEmpty(array)) {
            throw new ServiceException(message);
        }
    }

    public static void isEmpty(@Nullable Collection<?> collection, String message) {
        if (!CollectionUtils.isEmpty(collection)) {
            throw new ServiceException(message);
        }
    }

    public static void isEmpty(@Nullable Map<?, ?> map, String message) {
        if (!CollectionUtils.isEmpty(map)) {
            throw new ServiceException(message);
        }
    }
}
