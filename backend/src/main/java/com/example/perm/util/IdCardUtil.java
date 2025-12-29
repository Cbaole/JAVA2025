package com.example.perm.util;

import com.example.perm.common.Gender;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class IdCardUtil {
    private static final DateTimeFormatter BIRTHDAY_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public record Parsed(LocalDate birthday, Gender gender) {
    }

    public static Parsed parse18(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return new Parsed(null, Gender.UNKNOWN);
        }
        try {
            var birthStr = idCard.substring(6, 14);
            var birthday = LocalDate.parse(birthStr, BIRTHDAY_FMT);
            var genderCode = Character.getNumericValue(idCard.charAt(16));
            var gender = (genderCode % 2 == 0) ? Gender.FEMALE : Gender.MALE;
            return new Parsed(birthday, gender);
        } catch (Exception e) {
            return new Parsed(null, Gender.UNKNOWN);
        }
    }
}

