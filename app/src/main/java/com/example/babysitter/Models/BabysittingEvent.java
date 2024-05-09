package com.example.babysitter.Models;

public class BabysittingEvent {
    private String messageId;
    private String messageText;
    private String selectedDate;
    private String parentUid;
    private String babysitterUid;
    private Boolean status;
    public BabysittingEvent() {
    }

    // Constructor with parameters
    public BabysittingEvent(String parentUid,String babysitterUid, String messageText, String selectedDate) {
        this.messageText = messageText;
        this.selectedDate = selectedDate;
        this.parentUid=parentUid;
        this.babysitterUid=babysitterUid;
    this.status= null;
    }

    public String getMessageId() {
        return messageId;
    }

    public BabysittingEvent setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public String getParentUid() {
        return parentUid;
    }

    public String getBabysitterUid() {
        return babysitterUid;
    }

    public BabysittingEvent setParentUid(String parentUid) {
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

    public Boolean getStatus() {
        return status;
    }

    public BabysittingEvent setStatus(boolean status) {
        this.status = status;
        return this;
    }
}
