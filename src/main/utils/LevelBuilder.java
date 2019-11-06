package src.main.utils;

import java.awt.*;
import java.util.ArrayList;
import java.awt.image.BufferedImage;

import src.main.objects.*;
import src.main.basetypes.*;
import src.main.basetypes.Rectangle;
import src.main.Config;

public class LevelBuilder {
    static ArrayList<GameObject> gameObjects;
    static int w;
    static int h;
    static Integer startTileX = null;
    static boolean foundEndTile = false;
    static BufferedImage levelMap;
    static BufferedImage emptyImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    public static void addGroundCollider(int x, int y) {                 
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

            gameObjects.add(new GameObject("groundCollider", emptyImg, rect));
            
            startTileX = null;
            foundEndTile = false;
        }
    }

    public static void addBrick(int x, int y, GameObject item) {
        Brick brick = new Brick(
            new Rectangle(
                x * Config.TILE_SIZE, 
                y * Config.TILE_SIZE + 24, 
                Config.TILE_SIZE, 
                Config.TILE_SIZE
            ),
            item
        );
        gameObjects.add(item);
        gameObjects.add(brick);
    }

    public static void addPipe(int x, int y) {
        int h = Config.FRAME_SIZE[1] - y;
        Rectangle rect = new Rectangle(
            x * Config.TILE_SIZE, 
            y * Config.TILE_SIZE + 24, 
            96,
            h
        );

        gameObjects.add(new GameObject("pipe", emptyImg, rect));
    }

    public static void addGoomba(int x, int y) {
        Goomba goomba = new Goomba(new Rectangle(
            x * Config.TILE_SIZE, 
            y * Config.TILE_SIZE + 24, 
            Config.TILE_SIZE, 
            Config.TILE_SIZE
        ));
        gameObjects.add(goomba);
    }

    public static void addQuestion(int x, int y, GameObject item) {
        Question question = new Question(
            new Rectangle(
                x * Config.TILE_SIZE, 
                y * Config.TILE_SIZE + 24, 
                Config.TILE_SIZE, 
                Config.TILE_SIZE
            ),
            item
        );
        gameObjects.add(item);
        gameObjects.add(question);
    }

    public static void addTurtle(int x, int y) {
        Turtle turtle = new Turtle(new Rectangle(
            x * Config.TILE_SIZE,
            y * Config.TILE_SIZE,
            Config.TILE_SIZE,
            72
        ));
        gameObjects.add(turtle);
    }

    public static void addFlagPole(int x, int y) {
        FlagPole flagPole = new FlagPole(new Rectangle(
            x * Config.TILE_SIZE,
            y * Config.TILE_SIZE - 8 * Config.TILE_SIZE,
            Config.TILE_SIZE,
            456
        ));
        gameObjects.add(flagPole);
    }

    public static void addWinTrigger(int x, int y) {
        Trigger winTrigger = new Trigger(Config.WIN_TRIGGER_TAG, new Rectangle(
            x * Config.TILE_SIZE,
            y * Config.TILE_SIZE,
            Config.TILE_SIZE,
            Config.TILE_SIZE
        ));
        gameObjects.add(winTrigger);
    }

    public static GameObject getCoin(int x, int y) {
        return new Coin(new Rectangle(
            x * Config.TILE_SIZE,
            y * Config.TILE_SIZE + 24, 
            Config.TILE_SIZE, 
            Config.TILE_SIZE
        ));
    }

    public static GameObject getSuperMushroom(int x, int y) {
        return new SuperMushroom(new Rectangle(
            x * Config.TILE_SIZE,
            y * Config.TILE_SIZE + 24, 
            Config.TILE_SIZE, 
            Config.TILE_SIZE
        ));
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
                
                if (color.equals(Color.BLUE)) {                    
                    // Static ground colliders, which are grouped together for optimization
                    addGroundCollider(x, y);
                } else if (color.equals(new Color(100, 100, 100))) {
                    addBrick(x, y, getCoin(x,y));
                } else if (color.equals(Color.RED)) {
                    addPipe(x, y);
                } else if (color.equals(new Color(124, 66, 0))) {
                    addGoomba(x, y);
                } else if (color.equals(Color.YELLOW)) {
                    addQuestion(x, y, getCoin(x,y));
                } else if (color.equals(new Color(100, 255, 100))) {
                    addQuestion(x, y, getSuperMushroom(x, y));
                } else if (color.equals(new Color(79, 32, 207))) {
                    addTurtle(x, y);
                } else if (color.equals(new Color(203, 26, 141))) {
                    addFlagPole(x, y);
                } else if (color.equals(new Color(40, 251, 47))) {
                    addWinTrigger(x, y);
                }
            }
        }

        return gameObjects;
    }
}

// for y in range(0, level_1.size[1]):
//     for x in range(0, level_1.size[0]):

//         color = level_1.getpixel((x, y))
//         pos = Vector2(x * c.TILE_SIZE, y * c.TILE_SIZE + 24)

//         #Yellow = Question tile with coin as item
//         elif color == c.YELLOW:
//             coin_rect = Rectangle(Vector2(pos.x, pos.y), 48, 42)
//             contents = Coin(coin_rect)
//             coins.append(contents)
//             rect = Rectangle(pos, c.TILE_SIZE, c.TILE_SIZE)
//             dynamic_colliders.append(Question(rect, contents))

//         #Green = Question tile with mushroom as item
//         elif color == c.GREEN:
//             mushroom_rect = Rectangle(Vector2(pos.x, pos.y), c.TILE_SIZE, c.TILE_SIZE)
//             contents = Super_Mushroom(mushroom_rect, Vector2(c.MUSHROOM_START_VEL_X, 0))
//             super_mushrooms.append(contents)
//             rect = Rectangle(pos, c.TILE_SIZE, c.TILE_SIZE)
//             dynamic_colliders.append(Question(rect, contents))

//         #Brown = Goomba
//         elif color == c.BROWN:
//             rect = Rectangle(pos, c.TILE_SIZE, c.TILE_SIZE)
//             enemies.append(Goomba(rect, Vector2()))

//         elif color == c.PURPLE:
//             rect = Rectangle(Vector2(pos.x, pos.y - 24), 48, 72)
//             enemies.append(Turtle(rect, Vector2()))
