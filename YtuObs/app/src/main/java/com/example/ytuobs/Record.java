package com.example.ytuobs;

public class Record {
    private String course;
    private String email;
    private int day;
    private int hour;
    private String message;
    private int minute;
    private int month;
    private int second;
    private int year;

    // Boş yapıcı metod (Firebase'in ihtiyacı var)
    public Record() {
    }

    public Record(String course, String message, String email, int year, int month, int day, int hour, int minute, int second) {
        this.course = course;
        this.email = email;
        this.day = day;
        this.hour = hour;
        this.message = message;
        this.minute = minute;
        this.month = month;
        this.second = second;
        this.year = year;
    }

    // Getters ve Setters
    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    public String getDate() {
        return String.format("%02d/%02d/%04d %02d:%02d ", day, month, year,hour,second);
    }
}


