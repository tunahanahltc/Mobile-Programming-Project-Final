package com.example.ytuobs;

public class RecordAdmin {
    private String course;
    private String email;
    private String message;
    private int day;
    private int hour;
    private int minute;
    private int month;
    private int second;
    private int year;

    public RecordAdmin() {
        // Firestore'un veri dönüşümü için boş yapıcı
    }

    public RecordAdmin(String course, String email, String message, int day, int hour, int minute, int month, int second, int year) {
        this.course = course;
        this.email = email;
        this.message = message;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.month = month;
        this.second = second;
        this.year = year;
    }

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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
}
