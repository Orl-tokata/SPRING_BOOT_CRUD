package com.fullstack.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    // Define multiple formatters
    public static final DateTimeFormatter FORMAT_YYYY_MM_DD_HH_MM_SS =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter FORMAT_DD_MM_YYYY =
    DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final DateTimeFormatter FORMAT_MMM_DD_YYYY =
    DateTimeFormatter.ofPattern("MMM dd, yyyy");

    public static final DateTimeFormatter FORMAT_FULL_WITH_AM_PM =
    DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");

    /**
    * Format LocalDateTime using provided formatter
    */
    public static String format(LocalDateTime dateTime, DateTimeFormatter formatter) {
        return dateTime != null ? dateTime.format(formatter) : null;
    }

    /**
    * Shortcut methods
    */
    public static String formatDefault(LocalDateTime dateTime) {
        return format(dateTime, FORMAT_YYYY_MM_DD_HH_MM_SS);
    }

    public static String formatSlashDate(LocalDateTime dateTime) {
        return format(dateTime, FORMAT_DD_MM_YYYY);
    }

    public static String formatMonthName(LocalDateTime dateTime) {
        return format(dateTime, FORMAT_MMM_DD_YYYY);
    }

    public static String formatFullWithAmPm(LocalDateTime dateTime) {
        return format(dateTime, FORMAT_FULL_WITH_AM_PM);
    }
}
