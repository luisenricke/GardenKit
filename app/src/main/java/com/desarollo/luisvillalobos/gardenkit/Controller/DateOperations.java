package com.desarollo.luisvillalobos.gardenkit.Controller;

import java.util.Calendar;
import java.util.Date;

public class DateOperations {

    public static Date addMinute(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, i);
        return cal.getTime();
    }

    public static Date addHour(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, i);
        return cal.getTime();
    }

    public static Date addDay(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, i);
        return cal.getTime();
    }

    public static Date addMonth(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, i);
        return cal.getTime();
    }

    public static Date addYear(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, i);
        return cal.getTime();
    }

    public static Date subMinute(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, i * -1);
        return cal.getTime();
    }

    public static Date subHour(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, i * -1);
        return cal.getTime();
    }

    public static Date subDay(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, i * -1);
        return cal.getTime();
    }

    public static Date subMonth(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, i * -1);
        return cal.getTime();
    }

    public static Date subYear(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, i * -1);
        return cal.getTime();
    }

    public static int getSecond(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.SECOND);
    }

    public static int getMinute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }

    public static int getHour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public static void setSecond(Date date, int second) {
        if (date == null)
            return;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, second);
    }

    public static void setMinute(Date date, int minute) {
        if (date == null)
            return;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MINUTE, minute);
    }

    public static void setHour(Date date, int hour) {
        if (date == null)
            return;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hour);
    }

    public static void setDay(Date date, int day) {
        if (date == null)
            return;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, day);
    }

    public static void setMonth(Date date, int month) {
        if (date == null)
            return;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, month - 1);
    }

    public static void setYear(Date date, int year) {
        if (date == null)
            return;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.YEAR, year);
    }

    /**
     * Returns the given date with the time values cleared.
     */
    public static Date clearTime(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        //c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * Returns the given date with time set to the end of the day
     */
    public static Date getEnd(Date date) {
        if (date == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        //c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    //Checar para poder lanzar los errrores correctamente
    public static Date[] getNowAndPast(byte optionTime, int backwardTimeCount) throws IllegalArgumentException {
        Date[] dates = new Date[2];
        dates[0] = Calendar.getInstance().getTime();
        dates[1] = dates[0];

        if (backwardTimeCount > 0) {
            if (optionTime == 0)
                dates[1] = DateOperations.subMinute(dates[0], backwardTimeCount);
            else if (optionTime == 1)
                dates[1] = DateOperations.subHour(dates[0], backwardTimeCount);
            else if (optionTime == 2)
                dates[1] = DateOperations.subDay(dates[0], backwardTimeCount);
            else if (optionTime == 3)
                dates[1] = DateOperations.subMonth(dates[0], backwardTimeCount);
            else if (optionTime == 4)
                dates[1] = DateOperations.subYear(dates[0], backwardTimeCount);
            else
                throw new IllegalArgumentException("Invalid option of time");

            dates[0] = getEnd(dates[0]);
            dates[1] = clearTime(dates[1]);

            return dates;
        } else
            throw new IllegalArgumentException("Invalid backward time count");
    }

}
