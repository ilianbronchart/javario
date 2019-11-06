package src.main.utils;

import java.awt.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

import src.main.objects.*;
import src.main.basetypes.*;
import src.main.basetypes.Rectangle;
import src.main.Config;

public class LevelBuilder {
    private static ArrayList<GameObject> gameObjects;
    private static int w;
    private static int h;
    private static Integer startTileX = null;
    private static boolean foundEndTile = false;
    private static BufferedImage levelMap;

    private static void addGroundCollider(int x, int y) {    
        x /= Config.TILE_SIZE;
        y /= Config.TILE_SIZE;
        
        if (startTileX == null) {
            startTileX = x;
        }
        if (!foundEndTile) {
            foundEndTile = x + 1 > w; // Adjacent pixel is outside of the level

            Color adjacentPixelColor = new Color(levelMap.getRGB(x + 1, y));
            if (!adjacentPixelColor.equals(Color.BLUE)) {
                foundEndTile = true;
            }
        }
        if (foundEndTile) {                        
            Rectangle rect = new Rectangle(
                startTileX * Config.TILE_SIZE,
                y * Config.TILE_SIZE + 24,
                (x - startTileX + 1) * Config.TILE_SIZE,
                Config.TILE_SIZE
            );

            gameObjects.add(new GameObject("groundCollider", null, rect));
            
            startTileX = null;
            foundEndTile = false;
        }
    }

    private static void addBrick(int x, int y, GameObject item) {
        Brick brick = new Brick(
            new Rectangle(x, y + 24, Config.TILE_SIZE, Config.TILE_SIZE),
            item
        );
        gameObjects.add(item);
        gameObjects.add(brick);
    }

    private static void addPipe(int x, int y) {
        int h = Config.FRAME_SIZE[1] - y;
        gameObjects.add(new GameObject(Config.PIPE_TAG, null, new Rectangle(x, y + 24, 96, h)));
    }

    private static void addGoomba(int x, int y) {
        Goomba goomba = new Goomba(new Rectangle(x, y + 24, Config.TILE_SIZE, Config.TILE_SIZE));
        gameObjects.add(goomba);
    }

    private static void addQuestion(int x, int y, GameObject item) {
        Question question = new Question(
            new Rectangle(x, y + 24, Config.TILE_SIZE, Config.TILE_SIZE),
            item
        );
        gameObjects.add(item);
        gameObjects.add(question);
    }

    private static void addTurtle(int x, int y) {
        Turtle turtle = new Turtle(new Rectangle(x, y, Config.TILE_SIZE, 72));
        gameObjects.add(turtle);
    }

    private static void addFlagPole(int x, int y) {
        FlagPole flagPole = new FlagPole(new Rectangle(x, y - 8, Config.TILE_SIZE, 456));
        gameObjects.add(flagPole);
    }

    private static void addWinTrigger(int x, int y) {
        Trigger winTrigger = new Trigger(
            Config.WIN_TRIGGER_TAG, 
            new Rectangle(x, y, Config.TILE_SIZE, Config.TILE_SIZE
        ));
        gameObjects.add(winTrigger);
    }

    private static GameObject getCoin(int x, int y) {
        return new Coin(new Rectangle(x, y + 24, Config.TILE_SIZE, Config.TILE_SIZE));
    }

    private static GameObject getSuperMushroom(int x, int y) {
        return new SuperMushroom(new Rectangle(x, y + 24, Config.TILE_SIZE, Config.TILE_SIZE));
    }

    public static ArrayList<GameObject> buildLevel (BufferedImage map) {
        levelMap = map;
        gameObjects = new ArrayList<GameObject>();
        w = levelMap.getWidth();
        h = levelMap.getHeight();
        startTileX = null;
        foundEndTile = false;
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int pixelData = levelMap.getRGB(x, y);
                Color color = new Color(pixelData);
                x *= Config.TILE_SIZE;
                y *= Config.TILE_SIZE;
                
                if (color.equals(colors.groundCollider)) {
                    addGroundCollider(x, y);
                }
                else if (color.equals(colors.brick)) {
                    addBrick(x, y, getCoin(x,y));
                } 
                else if (color.equals(colors.pipe)) {
                    addPipe(x, y);
                } 
                else if (color.equals(colors.goomba)) {
                    addGoomba(x, y);
                } 
                else if (color.equals(colors.coinQuestion)) {
                    addQuestion(x, y, getCoin(x,y));
                } 
                else if (color.equals(colors.mushroomQuestion)) {
                    addQuestion(x, y, getSuperMushroom(x, y));
                } 
                else if (color.equals(colors.turtle)) {
                    addTurtle(x, y);
                } 
                else if (color.equals(colors.flagPole)) {
                    addFlagPole(x, y);
                } 
                else if (color.equals(colors.winTrigger)) {
                    addWinTrigger(x, y);
                }

                x /= Config.TILE_SIZE;
                y /= Config.TILE_SIZE;
            }
        }

        return gameObjects;
    }

    private interface colors {
        final Color groundCollider = Color.BLUE;
        final Color brick = new Color(100, 100, 100);
        final Color pipe = Color.RED;
        final Color goomba = new Color(124, 66, 0);
        final Color coinQuestion = Color.YELLOW;
        final Color mushroomQuestion = new Color(100, 255, 100);
        final Color turtle = new Color(79, 32, 207);
        final Color flagPole = new Color(203, 26, 141);
        final Color winTrigger = new Color(40, 251, 47);
    }
}
