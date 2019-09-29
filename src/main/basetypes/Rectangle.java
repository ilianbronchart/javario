package src.main.basetypes;

public class Rectangle {
    public Vector2 pos;
    public int w;
    public int h;

    public Rectangle (float x, float y, int w, int h) {
        this.pos = new Vector2(x, y);
        this.w = w;
        this.h = h;
    }

    public boolean overlaps(Rectangle other) {
        return !(other.pos.x + other.w <= pos.x ||
                 other.pos.x >= pos.x + w ||
                 other.pos.y + other.h <= pos.y ||
                 other.pos.y >= pos.y + h);
    }
}