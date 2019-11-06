package src.main.scenes;

import java.awt.image.BufferedImage;

import src.main.Config;
import src.main.basetypes.Rectangle;
import src.main.basetypes.*;
import src.main.globals.SpriteAtlas;
import src.main.globals.Keys;

public class MainMenu extends Scene {
    public MainMenu(){
        super(new Vector2(), Config.FRAME_SIZE[0], Config.FRAME_SIZE[1], 0);
        background = SpriteAtlas.MainMenu.background;
        gameObjects.add(new Selector());
    }

    private class Selector extends GameObject {
        private int selectedOption = 0;

        public Selector() {
            super("selector", SpriteAtlas.MainMenu.selector, Config.MainMenu.SELECTOR_RECT);
        }

        public void update() {
            if (Keys.up && !Keys.upPressed || Keys.down && !Keys.downPressed) {
                selectedOption += 1;
            } 
            
            if (Keys.enter && selectedOption % 2 == 0) {
                triggerScene(Config.Scenes.LEVEL_ONE);
            }

            rect.pos = Config.MainMenu.SELECTOR_POSITIONS[selectedOption % 2];
        }
    }
}
