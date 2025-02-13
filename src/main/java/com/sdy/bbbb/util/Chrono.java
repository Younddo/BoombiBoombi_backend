package com.sdy.bbbb.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Chrono {

    public static long dPlus(LocalDateTime dayBefore) {
        return ChronoUnit.DAYS.between(dayBefore, LocalDateTime.now());
    }

    public static long dMinus(LocalDateTime dayAfter) {
        return ChronoUnit.DAYS.between(dayAfter, LocalDateTime.now());
    }

    public static String timesAgo(LocalDateTime dayBefore) {
        long gap = ChronoUnit.MINUTES.between(dayBefore, LocalDateTime.now());
        String word = "";
        if (gap == 0) {
            word = "방금 전";
        } else if (gap < 60) {
            word = gap + "분 전";
        } else if (gap < 60 * 24) {
            word = (gap / 60) + "시간 전";
        } else if (gap < 60 * 24 * 10) {
            word = (gap / 60 / 24) + "일 전";
        } else if (gap < 60 * 24 * 7 * 5) {
            word = (gap / 60 / 24 / 7) + "주 전";
        } else {
            word = dayBefore.format(DateTimeFormatter.ofPattern("MM월 dd일"));
        }
        return word;
    }

    public static String timesAgoForRoom(LocalDateTime localDateTime) {
        String word = "";
        LocalDate day = localDateTime.toLocalDate();
        if (day.compareTo(LocalDate.now()) == 0) {
            word = localDateTime.format(DateTimeFormatter.ofPattern("a h:mm").withLocale(Locale.forLanguageTag("ko")));
        } else if (day.compareTo(LocalDate.now()) == -1) {
            word = "어제";
        } else {
            word = localDateTime.format(DateTimeFormatter.ofPattern("M월 d일"));
        }

        return word;
    }

    public static String customForm(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern("MM월 dd일"));
    }
}
