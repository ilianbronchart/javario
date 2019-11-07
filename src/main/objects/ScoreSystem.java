package src.main.objects;

import java.awt.*;
import src.main.basetypes.Rectangle;
import src.main.globals.SpriteAtlas;
import src.main.Config;
import src.main.Main;
import src.main.basetypes.*;
import src.main.globals.Time;

public class ScoreSystem extends GameObject {
    private static String scoreRepresentation = "000000";
    private static String coinsRepresentation = "00";
    private static String timeRepresentation;

    private static int score = 0;
    private static int coins = 0;
    private static int time;

    private float timer = 0;
    private static boolean addTimeScore = false;
    
    private Vector2 scorePos = new Vector2(66, 49);
    private Vector2 coinsPos = new Vector2(306, 49);
    private Vector2 timerPos = new Vector2(610, 49);

    public ScoreSystem() {
        super(null, SpriteAtlas.ScoreSystem.scoreBanner, new Rectangle());
        hasCollider = false;
        hasSpecializedRendering = true;
        fixedRender = true;
    }

    public void update() {
        if (time == 0) { return; }

        timer += Time.deltaTime;

        if (addTimeScore && timer > 3 * Time.deltaTime) {
            addScore(Config.TIME_SCORE);
            decrementTimer();
        }
        else if (timer > 17 * Time.deltaTime) {
            timer = 0;
            decrementTimer();
        }
    }

    public static void addTimeScore() {
        addTimeScore = true;
    }

    public static int getTime() {
        return time;
    }

    public static void setTime(int value) {
        time = value;
        timeRepresentation = getRepresentationOf(time, String.valueOf(time).length());
    }

    public void setScore(int value) {
        score = value;
    }

    public static void addScore(int addedScore) {
        score += addedScore;
        scoreRepresentation = getRepresentationOf(score, scoreRepresentation.length());
    }


    public static void addCoin() {
        coins++;
        coinsRepresentation = getRepresentationOf(coins, coinsRepresentation.length());
    }

    private void decrementTimer() {
        time--;
        timeRepresentation = getRepresentationOf(time, timeRepresentation.length());
    }

    private static String getRepresentationOf(int value, int representationLength) {
        // Add leading zeros if needed

        int remainingDigits = representationLength - String.valueOf(value).length();
        if (remainingDigits != 0) {
            return new String(new char[remainingDigits]).replace("\0", "0") + value;
        }

        return String.valueOf(value);
    }

    public void render(Graphics2D g2d) {
        renderNumber(g2d, scoreRepresentation, scorePos);
        renderNumber(g2d, coinsRepresentation, coinsPos);
        renderNumber(g2d, timeRepresentation, timerPos);
    }

    private void renderNumber(Graphics2D g2d, String number, Vector2 pos) {
        // Render all digits separately
        
        for (int i = 0; i < number.length(); i++) {
            int digit = Integer.valueOf(Character.toString(number.charAt(i)));
            g2d.drawImage(
                SpriteAtlas.ScoreSystem.digits[digit],
                (int) pos.x + i * 24, (int) pos.y,
                Main.canvas
            );
        }
    }
}