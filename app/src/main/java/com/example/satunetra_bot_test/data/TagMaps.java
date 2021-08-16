package com.example.satunetra_bot_test.data;

import com.example.satunetra_bot_test.model.Feel;
import com.example.satunetra_bot_test.model.Instruction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TagMaps {
    Map<String, Feel> tagMap;

    public TagMaps() {
        tagMap = new HashMap<>();
    }

    public Map<String, Feel> readTags(){
        HashMap<String, Instruction> m1 = new HashMap<String, Instruction>()
        {{
            put("b01", new Instruction("audioterapi", new ArrayList<>()));
            put("b02", new Instruction("music", new ArrayList<>()));
        }};
        Feel val1 = new Feel(m1, "cemas atau khawatir", new String[]{"b01", "b02"});
        tagMap.put("a01", val1);

        HashMap<String, Instruction> m2 = new HashMap<String, Instruction>()
        {{
            put("b03", new Instruction("audioterapi",new ArrayList<>()));
            put("b04", new Instruction("music", new ArrayList<>()));
        }};
        Feel val2 = new Feel(m2, "stress", new String[]{"b03", "b04"});
        tagMap.put("a02", val2);

        HashMap<String, Instruction> m3 = new HashMap<String, Instruction>()
        {{
            put("b05", new Instruction("musik bertempo lambat", new ArrayList<>()));
            put("b06", new Instruction("musik dari suara alam", new ArrayList<>()));
        }};
        Feel val3 = new Feel(m3, "Insomnia atau susah tidur", new String[]{"b05", "b06"});
        tagMap.put("a03", val3);

        return  tagMap;
    }
}
