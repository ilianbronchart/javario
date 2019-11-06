package src.main.scenes;

import java.awt.*;
import java.awt.image.*;

import src.main.Config;
import src.main.basetypes.*;
import src.main.basetypes.Rectangle;
import src.main.globals.SpriteAtlas;
import src.main.objects.*;
import src.main.utils.LevelBuilder;
import src.main.objects.ScoreSystem;

public class LevelOne extends Scene {
    public LevelOne() {
        super(Config.LevelOne.CAMERA_START_POS, Config.FRAME_SIZE[0], Config.FRAME_SIZE[1], Config.LevelOne.MAX_SCROLL);
        gameObjects = LevelBuilder.buildLevel(SpriteAtlas.LevelOne.map);
        gameObjects.add(new ScoreSystem());
        gameObjects.add(new Mario("mario", Config.LevelOne.MARIO_RECT));

        background = SpriteAtlas.LevelOne.background;
        foreground = SpriteAtlas.LevelOne.foreground;
        foregroundPos = Config.LevelOne.FOREGROUND_POS;
        
        ScoreSystem.setTime(Config.LevelOne.TIME);
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
        renderForeground(g2d);
    }
}