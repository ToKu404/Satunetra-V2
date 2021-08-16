package com.example.satunetra_bot_test.model;

import java.util.List;

public class Instruction {
    private final List<String> listUrl;
    private String value;

    public Instruction(String value,  List<String> listUrl) {
        this.value = value;
        this.listUrl = listUrl;
    }

    public List<String> getListUrl() {
        return listUrl;
    }

    public void addURL(List<String> listUrl) {
        try {
            this.listUrl.addAll(listUrl);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
