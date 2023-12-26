package com.example.studentsrecordbook;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Exam implements Serializable {

    private final String user;
    private final String name;
    private final int mark;
    private final int cfu;
    private final Calendar date;

    public Exam(String user, String name, int mark, int cfu, Calendar date) {
        this.user = user;
        this.name = name;
        this.mark = mark;
        this.cfu = cfu;
        this.date = date;
    }

    public Exam(String user, String name, int mark, int cfu, String date) throws ParseException {
        this.user = user;
        this.name = name;
        this.mark = mark;
        this.cfu = cfu;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss", Locale.ITALY);
        Date parsedDate = format.parse(date);

        Calendar calendar = GregorianCalendar.getInstance(Locale.ITALY);
        assert parsedDate != null;
        calendar.setTime(parsedDate);

        this.date = calendar;
    }

    public String getUser() {
        return user;
    }

    public String getName() {
        return name;
    }

    public int getCfu() {
        return cfu;
    }

    public int getMark() {
        return mark;
    }

    public Calendar getDate() {
        return date;
    }

    public String getDateLiteral() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss", Locale.ITALY);
        return format.format(date.getTime());
    }
}
