package com.gkmonk.pos.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {

    public static DateTimeFormatter findDateFormat(String dateString) {
        List<String> possibleFormats = new ArrayList<String>();
        possibleFormats.add("yyyy-MM-dd"); // Year-Month-Day
        possibleFormats.add("dd-MM-yyyy"); // Day-Month-Year
        possibleFormats.add("yyyy-MM-dd");
        possibleFormats.add("MM/dd/yyyy"); // Month/Day/Year
        possibleFormats.add("yyyy-MM-dd HH:mm:ss"); // Year-Month-Day Hour:Minute:Second
        possibleFormats.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
        possibleFormats.add("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        possibleFormats.add("yyyy-MM-dd'T'HH:mm:ssX");
        possibleFormats.add("dd MMMM yyyy");
        possibleFormats.add("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        possibleFormats.add("yyyy-MM-dd'T'HH:mm:ss.SSS");
        possibleFormats.add("ddMMyyyy");

        for (String format : possibleFormats) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
                formatter.parse(dateString);
                return formatter;
            } catch (DateTimeParseException e) {
                // Ignore parse exception for this format, try the next one
            }
        }
        try {
            DateTimeFormatter.ISO_DATE_TIME.parse(dateString);
            return DateTimeFormatter.ISO_DATE_TIME;
        } catch (DateTimeParseException e) {
            // Ignore parse exception for this format, try the next one
        }
        throw new IllegalArgumentException("Unsupported date format: " + dateString);
    }

    public static String getNextUpdate(long nextUpdate) {
        LocalDate date = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        date = date.plusDays(nextUpdate);
        return date.toString();
    }

    public static String getTodaysDateInIST(){
        return LocalDateTime.now(ZoneId.of("Asia/Kolkata")).toString();
    }

    public static void main(String[] args) {
        System.out.println("Parsed date: " + getNextUpdate(1));
    }

   public static String convertToDBFormat(String date) {
       try {
           // Parse the input date string
           DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
           LocalDate parsedDate = LocalDate.parse(date, inputFormatter);

           // Convert to the desired format
           DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
           return parsedDate.format(outputFormatter);
       } catch (DateTimeParseException e) {
           throw new IllegalArgumentException("Invalid date format: " + date);
       }
   }
}
