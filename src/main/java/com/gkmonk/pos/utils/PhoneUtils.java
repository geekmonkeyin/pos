package com.gkmonk.pos.utils;

public class PhoneUtils {

    public static String formatPhoneNo(String phone) {
        phone = removeWhiteSpace(phone);
        phone = addCountryCode(phone);
        return phone;
    }

    public static String addCountryCode(String phoneNo) {
        phoneNo = phoneNo.replaceAll("[+]", "");
        if (phoneNo.length() == 10) {
            return "91" + phoneNo;
        }
        if (phoneNo.charAt(0) == '0') {
            return "91" + phoneNo.substring(1);
        }
        return phoneNo;
    }

    public static String removeWhiteSpace(String text) {

        return text.replaceAll(" ", "");
    }

}
