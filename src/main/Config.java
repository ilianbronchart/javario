package src.main;

import java.awt.*;
import src.main.basetypes.*;
import src.main.basetypes.Rectangle;

public abstract class Config{

    // Window config
    public final static int[] FRAME_SIZE = {744, 672};
    public final static String FRAME_TITLE = "Javario Brothas";

    // Scenes
    public final static class Scenes {
        public final static String MAIN_MENU = "MAIN_MENU";
        public final static String LEVEL_ONE = "LEVEL_ONE";
    }

    // Colors
    public final static Color BACKGROUND_COLOR = new Color(107, 140, 255);
    
    // Distance from left side of the screen, from which camera starts following
    public final static int CAMERA_FOLLOW_TRESHOLD = 300;
    public final static int MAX_JUMP_HEIGHT = 140;
    public final static int TILE_SIZE = 48;

    // Accelerations / Friction
    public final static float MARIO_ACCELERATION = 0.0005f;
    public final static float GRAVITY = 0.002f;
    public final static float DECEL_FRICTION = 0.95f;
    public final static float BRAKE_FRICTION = 0.90f;
    
    // Velocities for different events
    public final static float JUMP_VEL = -0.9f;
    public final static float ENEMY_SQUISH_JUMP_VEL = -0.4f;
    public final static float WIN_JUMP_VEL = -0.4f;
    public final static float BUMP_VEL = 0.1f; // VEL that mario gets when he bumps the underside of a collider
    public final static float MARIO_MAX_VEL = 0.35f;
    public final static float MIN_STOP_VEL = 0.02f;
    public final static float ENTITY_START_VEL_X = 0.1f;
    public final static float GOOMBA_KNOCKED_VEL = -0.8f;
    public final static float COIN_BOUNCE_SPEED = 0.6f;

    // Tags
    public final static String MARIO_TAG = "mario";
    public final static String GOOMBA_TAG = "goomba";
    public final static String TURTLE_TAG = "turtle";
    public final static String BRICK_TAG = "brick";
    public final static String BRICK_FRAGMENT_TAG = "brick_fragment";
    public final static String QUESTION_TAG = "question";
    public final static String COIN_TAG = "coin";
    public final static String SUPER_MUSHROOM_TAG = "super_mushroom";
    public final static String FLAGPOLE_TAG = "flagpole";
    public final static String WIN_TRIGGER_TAG = "win_trigger";
    public final static String SCORESYSTEM_TAG = "score_system";
    public final static String PIPE_TAG = "pipe";
    
    // SCORE
    public final static int TURTLE_SCORE = 300;
    public final static int GOOMBA_SCORE = 1000;
    public final static int MUSHROOM_SCORE = 100;
    public final static int TIME_SCORE = 1000;

    public static class MainMenu {
        public final static Rectangle SELECTOR_RECT = new Rectangle(239, 404, 0, 0);
        public final static Vector2[] SELECTOR_POSITIONS = {
            new Vector2(239, 404),
            new Vector2(239, 448)
        };
    }

    public static class LevelOne {
        public final static int TIME = 400;
        public final static int MAX_SCROLL = 9350; // Furthest distance the camera can scroll to
        public final static Vector2 CAMERA_START_POS = new Vector2();
        public final static Vector2 FOREGROUND_POS = new Vector2(9840, 504);
        public final static Rectangle MARIO_RECT = new Rectangle(138, 552, 36, 48);
    }
}