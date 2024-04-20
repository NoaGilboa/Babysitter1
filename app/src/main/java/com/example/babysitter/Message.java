package com.example.babysitter;

public class Message {
    private String messageText;
    private String selectedDate;
    private String parentUid;
    public Message() {
    }

    // Constructor with parameters
    public Message(String parentUid, String messageText, String selectedDate) {
        this.messageText = messageText;
        this.selectedDate = selectedDate;
        this.parentUid=parentUid;
    }

    public String getParentUid() {
        return parentUid;
    }

    public Message setParentUid(String parentUid) {
        this.parentUid = parentUid;
        return this;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }


}
