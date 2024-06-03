package com.example.ytuobs;

import java.io.Serializable;

public abstract class Member implements Serializable {

    private String e_mail,tel_no,name;
    private String statu;
    private String educateInform;



    public Member(String e_mail, String name) {
        this.e_mail = e_mail;
        this.statu = "";
        this.tel_no = "";

        this.name = name;
        this.educateInform = "";
    }

    @Override
    public String toString() {
        return "Member{" +
                "e_mail='" + e_mail + '\'' +
                ", tel_no='" + tel_no + '\'' +
                ", name='" + name + '\'' +
                ", statu='" + statu + '\'' +
                ", educateInform='" + educateInform + '\'' +
                '}';
    }

    public String getStatu() {
        return statu;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setStatu(String statu) {
        this.statu = statu;
    }

    public String getEducateInform() {
        return educateInform;
    }

    public void setEducateInform(String educateInform) {
        this.educateInform = educateInform;
    }
    public String getE_mail() {
        return e_mail;
    }

    public String getTel_no() {
        return tel_no;
    }



    public String getName() {
        return name;
    }

    public void setTel_no(String tel_no) {
        this.tel_no = tel_no;
    }


}
