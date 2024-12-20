package com.example.application.enums;


public enum Mood {
    OK("Ok", "#D3D3D3"),          // Gray
    GOOD("Good", "#A0D99C"),      // Light green
    VERY_GOOD("Very Good", "#A0D99C"),        // Light red
    VERY_BAD("Very Bad", "#FF0000"), // Red
    HAPPY("HAPPY", "#FFA500"), // Orange
    BAD("Bad", "#000000");  // Black

    private final String label;
    private final String colorCode;

    Mood(String label, String colorCode) {
        this.label = label;
        this.colorCode = colorCode;
    }

    public String getLabel() {
        return label;
    }

    public String getColorCode() {
        return colorCode;
    }
}
