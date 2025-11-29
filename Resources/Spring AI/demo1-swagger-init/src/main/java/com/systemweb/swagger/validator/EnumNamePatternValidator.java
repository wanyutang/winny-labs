package com.systemweb.swagger.validator;

import java.util.Arrays;

public class EnumNamePatternValidator {
    public static boolean isValidEnumName(Class<? extends Enum<?>> enumClass, String value) {
        if (value == null) return false;
        return Arrays.stream(enumClass.getEnumConstants()).anyMatch(e -> e.name().equals(value));
    }
}
