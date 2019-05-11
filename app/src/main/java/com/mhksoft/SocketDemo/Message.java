package com.mhksoft.SocketDemo;

public class Message {
    private String body;
    private Long time;
    private Boolean isServerMessage;

    public Message(String body, Long time, Boolean isServerMessage) {
        this.body = body;
        this.time = time;
        this.isServerMessage = isServerMessage;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Boolean getServerMessage() {
        return isServerMessage;
    }

    public void setServerMessage(Boolean serverMessage) {
        isServerMessage = serverMessage;
    }
}
