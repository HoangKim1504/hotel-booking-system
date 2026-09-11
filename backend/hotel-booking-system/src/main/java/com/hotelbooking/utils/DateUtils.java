package com.hotelbooking.utils;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@UtilityClass
public class DateUtils {

    public static LocalDateTime toLocalDateTime(Instant date) {
        return LocalDateTime.ofInstant(date, ZoneId.systemDefault());
    }

}
