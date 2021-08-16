package com.example.satunetra_bot_test.model;

import java.util.List;
import java.util.Map;

public class Feel {
    private Map<String, Instruction> child;
    private String value;
    private String[] instructionList;



    public Feel(Map<String, Instruction> child, String value, String[] instructionList) {
        this.child = child;
        this.value = value;
        this.instructionList = instructionList;
    }

    public String[] getInstructionList() {
        return instructionList;
    }

    public void setInstructionList(String[] instructionList) {
        this.instructionList = instructionList;
    }

    public Map<String, Instruction> getChild() {
        return child;
    }

    public void setChild(Map<String, Instruction> child) {
        this.child = child;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

