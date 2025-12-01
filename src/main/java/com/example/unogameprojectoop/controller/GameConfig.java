package com.example.unogameprojectoop.controller;

public class GameConfig {
    // 1.0 = Sáng nhất, 0.0 = Tối om
    private static double brightnessLevel = 1.0;

    public static double getBrightness() {
        return brightnessLevel;
    }

    public static void setBrightness(double value) {
        brightnessLevel = value;
    }
}