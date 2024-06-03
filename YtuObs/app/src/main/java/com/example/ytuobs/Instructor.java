package com.example.ytuobs;

import java.io.Serializable;

public class Instructor extends Member implements Serializable {
    public Instructor(String e_mail, String name) {
        super(e_mail, name);
    }
}
