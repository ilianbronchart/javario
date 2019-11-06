package src.main.globals;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.*;

public class SpriteAtlas {
    private static BufferedImage getImage(String path) {
        path = "src/main/resources/graphics/" + path;

        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB); // Default, if image can't be read

        try {
            img = ImageIO.read(new File(path));
        } catch (Exception e) {
            System.out.println("Couldn't read file " + path);
            e.printStackTrace();
        }

        return img;
    }


    // For sprites that will be reused in multiple instances
    private static BufferedImage tileSet = getImage("tile_set.png");
    public static BufferedImage brick = tileSet.getSubimage(0, 0, 48, 48);
    public static BufferedImage superMushroom = tileSet.getSubimage(192, 48, 48, 48);
    public static BufferedImage flag = tileSet.getSubimage(336, 0, 48, 48);
    
    public static BufferedImage[] brickFragments = {
        tileSet.getSubimage(252, 12, 24, 24),
        tileSet.getSubimage(300, 12, 24, 24)
    };
    
    public static BufferedImage[] goomba = {
        tileSet.getSubimage(0, 48, 48, 48),
        tileSet.getSubimage(48, 48, 48, 48),
        tileSet.getSubimage(96, 48, 48, 48),
        tileSet.getSubimage(144, 48, 48, 48)
    };

    public static BufferedImage[] question = {
        tileSet.getSubimage(48, 0,  48, 48),
        tileSet.getSubimage(96, 0,  48, 48),
        tileSet.getSubimage(144, 0,  48, 48),
        tileSet.getSubimage(192, 0,  48, 48)
    };

    public static BufferedImage[] coin = {
        tileSet.getSubimage(144, 126, 48, 42),
        tileSet.getSubimage(192, 126, 48, 42),
        tileSet.getSubimage(240, 126, 48, 42),
        tileSet.getSubimage(288, 126, 48, 42)
    };

    public static class Turtle {
        public static Image[] run = {
            tileSet.getSubimage(0, 96, 48, 72),
            tileSet.getSubimage(48, 96, 48, 72)
        };
        public static Image shell = tileSet.getSubimage(96, 126, 48, 42);
    }

    public static class ScoreSystem {
        public static BufferedImage scoreBanner = getImage("text_image.png");
        public static BufferedImage digitSet = getImage("digits.png");

        public static BufferedImage[] digits = {
            digitSet.getSubimage(0, 0, 24, 21),
            digitSet.getSubimage(24, 0, 24, 21),
            digitSet.getSubimage(24 * 2, 0, 24, 21),
            digitSet.getSubimage(24 * 3, 0, 24, 21),
            digitSet.getSubimage(24 * 4, 0, 24, 21),
            digitSet.getSubimage(24 * 5, 0, 24, 21),
            digitSet.getSubimage(24 * 6, 0, 24, 21),
            digitSet.getSubimage(24 * 7, 0, 24, 21),
            digitSet.getSubimage(24 * 8, 0, 24, 21),
            digitSet.getSubimage(24 * 9, 0, 24, 21),
        };
    }

    public static class Mario {
        public static Image marioDead = SpriteAtlas.tileSet.getSubimage(240, 168, 48, 48);
        public static Image invincibilitySprite = SpriteAtlas.tileSet.getSubimage(240, 48, 48, 48);

        // IDLE
        public static Image smallMarioIdle = SpriteAtlas.tileSet.getSubimage(288, 168, 48, 48);
        public static Image mediumMarioIdle = SpriteAtlas.tileSet.getSubimage(48, 327, 48, 72);
        public static Image bigMarioIdle = SpriteAtlas.tileSet.getSubimage(288, 216, 48, 96);

        // JUMP
        public static Image smallMarioJump = SpriteAtlas.tileSet.getSubimage(192, 168, 48, 48);
        public static Image bigMarioJump = SpriteAtlas.tileSet.getSubimage(192, 216, 48, 96);

        // BRAKE
        public static Image smallMarioBrake = SpriteAtlas.tileSet.getSubimage(144, 168, 48, 48);
        public static Image bigMarioBrake = SpriteAtlas.tileSet.getSubimage(144, 216, 48, 96);

        // CROUCH
        public static Image marioCrouch = SpriteAtlas.tileSet.getSubimage(240, 246, 48, 66);

        public static Image[] smallMarioFlag = {
            SpriteAtlas.tileSet.getSubimage(387, 168, 42, 48),
            SpriteAtlas.tileSet.getSubimage(339, 168, 42, 48)
        };
        
        public static Image[] bigMarioFlag = {
            SpriteAtlas.tileSet.getSubimage(387, 222, 42, 90),
            SpriteAtlas.tileSet.getSubimage(339, 222, 42, 90)
        };

        public static Image[] smallMarioRun = new Image[] {
            SpriteAtlas.tileSet.getSubimage(0, 168, 48, 48),
            SpriteAtlas.tileSet.getSubimage(48, 168, 48, 48),
            SpriteAtlas.tileSet.getSubimage(96, 168, 48, 48)
        };

        public static Image[] bigMarioRun = new Image[] {
            SpriteAtlas.tileSet.getSubimage(96, 216, 48, 96),
            SpriteAtlas.tileSet.getSubimage(48, 216, 48, 96),
            SpriteAtlas.tileSet.getSubimage(0, 216, 48, 96)
        };
    }

    public static class MainMenu {
        public static BufferedImage background = getImage("menu.png");
        public static BufferedImage selector = tileSet.getSubimage(394, 12, 24, 24);
    }

    public static class LevelOne {
        public static BufferedImage map = getImage("levelone.png");
        public static BufferedImage background = getImage("background.png");
        public static BufferedImage foreground = getImage("foreground.png");
    }
}