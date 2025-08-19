package com.gkmonk.pos.utils;

import java.time.LocalDate;

public class StringUtils {

    private StringUtils() {}

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String removeSpecialCharacters(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }

    public static String removeSpaces(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replaceAll("\\s", "");
    }

    public static String removeSpacesAndSpecialCharacters(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replaceAll("[^a-zA-Z0-9]", "").replaceAll("\\s", "");
    }

    public static String removeSpecialCharactersAndCapitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replaceAll("[^a-zA-Z0-9]", "").substring(0, 1).toUpperCase() + input.substring(1);
    }


    public static boolean isBlank(Object object) {
        return object != null && "".equalsIgnoreCase(String.valueOf(object));
    }

    public static boolean isNotBlank(String productId) {
        return productId != null && !productId.isEmpty();
    }

    public static String defaultIfBlank(String input, String defaultValue) {
        return StringUtils.isBlank(input) ? defaultValue : input;
    }

    public static String updateStartDate(String date) {
        return StringUtils.isBlank(date) ? LocalDate.now().minusDays(30).toString() : date.trim();
    }

    public static Double updateMinAmount(Double amount) {
        return amount == null ? 0.0d : amount;
    }

    public static Double updateMaxAmount(Double amount) {
        return amount == null ? 10000000.0d : amount;
    }

    public static String updateVendorName(String vendorName) {
        return StringUtils.isBlank(vendorName) ? POSConstants.EMPTY : vendorName.trim();
    }

    public static String updateEndDate(String date) {
        return StringUtils.isBlank(date) ? LocalDate.now().toString() : date.trim();
    }
}