package src.main.scenes;

import java.awt.*;
import java.awt.image.*;

import src.main.Config;
import src.main.basetypes.*;
import src.main.basetypes.Rectangle;
import src.main.objects.*;
import src.main.utils.LevelBuilder;

public class LevelOne extends Scene {
    public LevelOne() {
        super(9200, 0, Config.FRAME_SIZE[0], Config.FRAME_SIZE[1], 9000);
        gameObjects = LevelBuilder.buildLevel(Sprites.levelOne);

        //138
        gameObjects.add(new Mario("mario", new Rectangle(9200, 500, 36, 48)));
        background = Sprites.background;
        foreground = Sprites.foreground;
        foregroundPos = new Vector2(9840, 504);
    }

    @Override
    public void update() {
        updateGameObjects(gameObjects);
        physicsUpdate(gameObjects);
    }
    
    @Override
    public void render (Graphics2D g2d) {
        renderBackground(g2d);
        renderGameObjects(g2d, gameObjects);
        debugColliders(g2d, Color.GREEN);
        renderForeground(g2d);
    }

    static class Sprites extends SpriteSet {
        static BufferedImage levelOne = getBufferedImage("levelone.png");
        static BufferedImage background = getBufferedImage("background.png");
        static BufferedImage foreground = getBufferedImage("foreground.png");
    }
}