package com.example.satunetra_bot_test.model;

import java.util.Map;

public class Tag {
    private Map<String, String> child;
    private String value;

    public Tag(Map<String, String> child, String value) {
        this.child = child;
        this.value = value;
    }

    public String getChildName(String key){
        return child.get(key);
    }


    public boolean childEquals(String tag){
        return child.containsKey(tag);
    }



    public Map<String, String> getChild() {
        return child;
    }

    public void setChild(Map<String, String> child) {
        this.child = child;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

