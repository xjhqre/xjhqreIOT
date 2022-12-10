package com.xjhqre.common.utils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.xjhqre.common.exception.ServiceException;

public class BeanValidateUtils {
    private BeanValidateUtils() {}

    public static <T> void validate(T data, Class<?>... groups) {
        List<String> validateResult = validateResult(data, groups);
        if (CollectionUtils.isNotEmpty(validateResult)) {
            throw new ServiceException(StringUtils.join(validateResult, ","));
        }
    }

    public static <T> List<String> validateResult(T data, Class<?>... groups) {
        Validator globalValidator = SpringUtils.getBean(Validator.class);
        Set<ConstraintViolation<T>> set = globalValidator.validate(data, groups);
        return set != null && !set.isEmpty() ? set.stream().map(constraintViolation -> String.format("%s:%s",
            constraintViolation.getPropertyPath(), constraintViolation.getMessage())).collect(Collectors.toList())
            : Collections.emptyList();
    }
}