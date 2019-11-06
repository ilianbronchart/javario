package src.main.objects;

import src.main.basetypes.GameObject;
import src.main.basetypes.Rectangle;

public class Trigger extends GameObject {
    public Trigger(String tag, Rectangle rect) {
        super(tag, null, rect);
        isEntity = true;
    }
}