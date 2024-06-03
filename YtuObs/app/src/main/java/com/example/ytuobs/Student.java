package com.example.ytuobs;

import java.io.Serializable;

public class Student extends Member implements Serializable {
    private String student_no;

    public Student(String e_mail, String name,String student_no) {
        super(e_mail, name);
        this.student_no = student_no;
    }

    public String getStudent_no() {
        return student_no;
    }


    @Override
    public String toString() {
        return "Student{" +
                "student_no='" + student_no + '\'' +
                '}';
    }
}

