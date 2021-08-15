package com.example.satunetra_bot_test.model;

import java.io.Serializable;

public class Message implements Serializable {
    String id;
    String message;
    String time;

    public Message(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
