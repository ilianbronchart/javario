package src.main.globals;

public class Keys {
    private static boolean inputFrozen = false;

    public static boolean up = false;
    public static boolean upPressed = false;
    public static boolean left = false;
    public static boolean leftPressed = false;
    public static boolean down = false;
    public static boolean downPressed = false;
    public static boolean right = false;
    public static boolean rightPressed = false;
    public static boolean space = false;
    public static boolean spacePressed = false;
    public static boolean enter = false;
    public static boolean enterPressed = false;

    public static void freezeInput() {
        inputFrozen = true;
    }

    public static void unFreezeInput() {
        inputFrozen = false;
    }

    public static boolean inputFrozen() {
        return inputFrozen;
    }

    public static void resetKeys() {
        up = false;
        upPressed = false;
        left = false;
        leftPressed = false;
        down = false;
        downPressed = false;
        right = false;
        rightPressed = false;
        space = false;
        spacePressed = false;
        enter = false;
        enterPressed = false;
    }
}