package com.example.glpicking.program;

import java.util.List;

public enum AttributeVariable {

    POSITION("a_Position"),
    COLOR("a_Color");


    private final String name;

    AttributeVariable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static String[] toStringArray(List<AttributeVariable> attributes) {
        String[] programAttributes = new String[attributes.size()];
        for (int i = 0; i < attributes.size(); i++) {
            programAttributes[i] = attributes.get(i).getName();
        }
        return programAttributes;
    }
}
