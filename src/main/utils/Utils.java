package src.main.utils;

public class Utils {
    public static float clamp(float x, float a, float b) {
        return Math.max(a, Math.min(b, x));
    }
}