package com.example.satunetra_bot_test.data;

import com.example.satunetra_bot_test.model.Tag;

import java.util.HashMap;
import java.util.Map;

public class TagMaps {
    Map<String, Tag> tagMap;

    public TagMaps() {
        tagMap = new HashMap<>();
    }

    public Map<String, Tag> readTags(){
        HashMap<String, String> m1 = new HashMap<String, String>()
        {{
            put("b01", "audioterapi");
            put("b02", "musik");
        }};
        Tag val1 = new Tag(m1, "cemas atau khawatir");
        tagMap.put("a01", val1);

        HashMap<String, String> m2 = new HashMap<String, String>()
        {{
            put("b02", "audioterapi");
            put("b03", "musik");
        }};
        Tag val2 = new Tag(m2, "stress");
        tagMap.put("a02", val2);

        HashMap<String, String> m3 = new HashMap<String, String>()
        {{
            put("b04", "musik bertempo lambat");
            put("b05", "musik dari suara alam");
        }};
        Tag val3 = new Tag(m3, "Insomnia atau susah tidur");
        tagMap.put("a03", val3);

        return  tagMap;
    }
}
