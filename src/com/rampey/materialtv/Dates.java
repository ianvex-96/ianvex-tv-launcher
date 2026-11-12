package com.rampey.materialtv;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/** Indonesian date and 24-hour time, independent of the TV's system language. */
final class Dates {
    private static final String[] DAYS = {
            "Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu"};
    private static final String[] MONTHS = {
            "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"};

    private Dates() {
    }

    /** "Rabu, 11 November" */
    static String date(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return DAYS[c.get(Calendar.DAY_OF_WEEK) - 1] + ", " + c.get(Calendar.DAY_OF_MONTH) + " "
                + MONTHS[c.get(Calendar.MONTH)];
    }

    /** "17:09" */
    static String time(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return String.format(Locale.ROOT, "%02d:%02d", c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }
}
