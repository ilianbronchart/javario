package src.main.basetypes;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.awt.*;

public abstract class SpriteSet {
    // For sprites that will used only in a single

    protected static Image getImage(String path) {
        path = "src/main/resources/graphics/" + path;
        return Toolkit.getDefaultToolkit().getImage(path);
    }

    protected static BufferedImage getBufferedImage(String path) {
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
}
