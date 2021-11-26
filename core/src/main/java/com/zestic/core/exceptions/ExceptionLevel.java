package com.zestic.core.exceptions;

public enum ExceptionLevel {
    SERIOUS(1, "serious"),
    COMMON(2, "common"),
    SLIGHT(3, "slight");

    private int value;

    private String description;

    ExceptionLevel(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
