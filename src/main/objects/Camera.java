package src.main.objects;

import src.main.basetypes.*;
import src.main.Config;
import src.main.globals.Time;

public class Camera extends Rectangle {
    int maxScroll;

    public Camera(float x, float y, int w, int h, int maxScroll) {
        super(x, y, w, h);
        this.maxScroll = maxScroll;
    }

    public boolean contains(Rectangle rect) {
        // Check if camera horizontally contains a rectangle

        return ((rect.pos.x > pos.x && rect.pos.x < pos.x + Config.FRAME_SIZE[0]) ||
                (rect.pos.x + rect.w > pos.x && rect.pos.x + rect.w < pos.x + Config.FRAME_SIZE[1]));
    }

    public Vector2 toViewspace(Vector2 vect) {
        // Return vector relative to the camera
        return new Vector2(vect.x - pos.x, vect.y);
    }

    public void updatePosition(Vector2 marioPos, Vector2 marioVel) {
        //Update position of camera based on mario velocity and position
        if (pos.x < maxScroll) {
            if (marioPos.x > pos.x + Config.CAMERA_FOLLOW_TRESHOLD && marioVel.x > 0) {
                pos.x += marioVel.x * Time.deltaTime;
            }
        }
    }
}
