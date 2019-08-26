package com.example.utils;

public class Preconditions {
    public static void checkState(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
}
