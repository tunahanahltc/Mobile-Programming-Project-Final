package com.example.ytuobs;

public class Announcement {
    private String id;
    private String text;
    private String teacher;
    private String formattedDate;

    public Announcement() {
        // Firestore için boş yapıcı
    }

    public Announcement(String id, String text, String teacher, String formattedDate) {
        this.id = id;
        this.text = text;
        this.teacher = teacher;
        this.formattedDate = formattedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}