package com.example.studentsrecordbook;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;

public class User implements Serializable {

    private final String fullName;
    private final String matricola;
    private final String password;
    private final Calendar birthday;

    public User(String fullName, String matricola, String password, Calendar birthday) {
        this.fullName = fullName;
        this.matricola = matricola;
        this.password = password;
        this.birthday = birthday;
    }

    public User(String fullName, String matricola, String password, String birthday) throws ParseException {
        this.fullName = fullName;
        this.matricola = matricola;
        this.password = password;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss", Locale.ITALY);
        Date birthdayDate = format.parse(birthday);

        Calendar calendar = GregorianCalendar.getInstance(Locale.ITALY);
        assert birthdayDate != null;
        calendar.setTime(birthdayDate);


        this.birthday = calendar;
    }

    public String getFullName() {
        return fullName;
    }

    public Calendar getBirthday() {
        return birthday;
    }

    public String getBirthdayLiteral() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.sss", Locale.ITALY);
        return format.format(birthday.getTime());

    }

    public String getMatricola() {
        return matricola;
    }

    public String getPassword() {
        return password;
    }

    public Boolean matchPassword(String providedPassword) {
        return Objects.equals(password, providedPassword);
    }
}
