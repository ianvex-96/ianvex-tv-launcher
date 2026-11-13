package com.rampey.materialtv;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** 24-hour time and a written date, in Indonesian (default) or English. */
final class Dates {
    private static final String[] DAYS_ID = {
            "Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
    private static final String[] DAYS_EN = {
            "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private static final String[] MONTHS_ID = {
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"};
    private static final String[] MONTHS_EN = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    private Dates() {
    }

    /** "Rabu, 11 November" or "Wednesday, 11 November". */
    static String date(Date date, boolean english) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        String[] days = english ? DAYS_EN : DAYS_ID;
        String[] months = english ? MONTHS_EN : MONTHS_ID;
        return days[c.get(Calendar.DAY_OF_WEEK) - 1] + ", " + c.get(Calendar.DAY_OF_MONTH) + " "
                + months[c.get(Calendar.MONTH)];
    }

    /** Indonesian date, for callers without a context. */
    static String date(Date date) {
        return date(date, false);
    }

    /** "17:09" */
    static String time(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return String.format(Locale.ROOT, "%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }
}
