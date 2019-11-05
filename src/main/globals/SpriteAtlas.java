package src.main.globals;

import src.main.basetypes.SpriteSet;
import java.awt.image.BufferedImage;

public class SpriteAtlas extends SpriteSet {
    // For sprites that will be reused in multiple instances
    public static BufferedImage tileSet = getBufferedImage("tile_set.png");
    public static BufferedImage brick = tileSet.getSubimage(0, 0, 48, 48);
    public static BufferedImage superMushroom = tileSet.getSubimage(192, 48, 48, 48);
    
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
}