package com.example.ytuobs;

public class Reply {
    private String replyText;
    private String replier;
    private String formattedDate;

    // Boş constructor Firestore için gereklidir
    public Reply() {}

    public Reply(String replyText, String replier, String formattedDate) {
        this.replyText = replyText;
        this.replier = replier;
        this.formattedDate = formattedDate;
    }

    public String getReplyText() {
        return replyText;
    }

    public void setReplyText(String replyText) {
        this.replyText = replyText;
    }

    public String getReplier() {
        return replier;
    }

    public void setReplier(String replier) {
        this.replier = replier;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }
}