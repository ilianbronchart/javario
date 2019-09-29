package src.main.basetypes;

public class Vector2{
    public float x;
    public float y;

    public Vector2(float x, float y){
        this.x = x;
        this.y = y;
    }

    public Vector2 multiply(float val) {
        x *= val;
        y *= val;

        return this;
    }

    public Vector2 multiply(double val) {
        x *= val;
        y *= val;

        return this;
    }

    public void add(Vector2 other) {
        x += other.x;
        y += other.y;
    }
}