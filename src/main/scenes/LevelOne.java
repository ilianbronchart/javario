package src.main.scenes;

import java.awt.*;
import java.awt.image.*;

import src.main.basetypes.*;
import src.main.basetypes.Rectangle;
import src.main.objects.*;
import src.main.utils.LevelBuilder;

public class LevelOne extends Scene {
    public LevelOne() {
        super(true);
        camera = new Camera(0, 0, 744, 672, 9000);
        gameObjects = LevelBuilder.buildLevel(Sprites.levelOne);
        gameObjects.add(new Mario("mario", new Rectangle(138, 552, 36, 48)));
        background = Sprites.background;
    }

    @Override
    public void update() {
        updateGameObjects();
        physicsUpdate();
    }

    @Override
    public void render (Graphics2D g2d) {
        renderBackground(g2d);
        renderGameObjects(g2d);
        debugColliders(g2d, Color.GREEN);
    }

    static class Sprites extends SpriteSet {
        static BufferedImage levelOne = getBufferedImage("levelone.png");
        static BufferedImage background = getBufferedImage("background.png");
    }
}