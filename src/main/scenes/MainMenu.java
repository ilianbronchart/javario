package src.main.scenes;

import java.awt.image.BufferedImage;

import src.main.Config;
import src.main.basetypes.Rectangle;
import src.main.basetypes.*;
import src.main.globals.SpriteAtlas;
import src.main.globals.Keys;

public class MainMenu extends Scene {
    int selectorOption = 0;

    public MainMenu(){
        super(false);
        background = Sprites.background;
        gameObjects.add(new Selector());
    }

    static class Sprites extends SpriteSet {
        static BufferedImage background = getBufferedImage("menu.png");
        static BufferedImage selector = SpriteAtlas.tileSet.getSubimage(394, 12, 24, 24);
    }

    private class Selector extends GameObject {
        int selectedOption = 0;

        Vector2[] positions = {
            new Vector2(239, 404),
            new Vector2(239, 448)
        };

        public Selector() {
            super("selector", Sprites.selector, new Rectangle(239, 404, 0, 0));
        }

        public void update() {
            if (Keys.up && !Keys.upPressed || Keys.down && !Keys.downPressed) {
                selectedOption += 1;
            } 
            
            if (Keys.enter) {
                nextScene = Config.Scenes.LEVEL_ONE;
            }

            rect.pos = positions[selectedOption % 2];
        }
    }
}
