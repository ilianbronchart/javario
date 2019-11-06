package src.main.basetypes;

import java.awt.*;
import java.util.ArrayList;

import src.main.Config;
import src.main.globals.Time;
import src.main.utils.Utils;

public class GameObject {
    public String tag;

    public Image sprite;

    public Rectangle rect;
    public Vector2 vel = new Vector2(0, 0);
    public Vector2 spriteOffset = new Vector2(0, 0); // Offset between sprite and collider
    
    public boolean freezeMovement = false;
    public boolean gravity = false;
    public boolean isActivated = false; // Has entered the camera viewspace at some point
    public boolean isAwake = true; // True by default, can be turned to false for custom behavior
    public boolean hasCollider = true;
    public boolean isEntity = false; // Indicates whether the GameObject should interact with other entities
    public boolean flipSprite = false;
    
    public float friction = 1;
    public float acceleration = 0;
    public float maxVel = 0;

    private String triggeredScene; // GameObjects can trigger a scene change depending on events
    public boolean hasTriggeredScene;

    public ArrayList<GameObject> childGameObjects = new ArrayList<GameObject>();

    public String getTriggeredScene() {
        return triggeredScene;
    }

    public void triggerScene(String scene) {
        hasTriggeredScene = true;
        triggeredScene = scene;
    }

    public GameObject(String tag, Image sprite, Rectangle rect){
        this.tag = tag;
        this.sprite = sprite;
        this.rect = rect;
    }

    public void setSpriteOffset(String offsetType) {
        switch (offsetType) {
            case "CenterHorizontal":
                spriteOffset.x = -(sprite.getWidth(null) % rect.w) / 2;
                break;
            case "CenterVertical":
                spriteOffset.y = -(sprite.getHeight(null) % rect.h) / 2;
                break;
            default:
                System.out.println("Invalid offsetType value as argument to setSpriteOffset: " + offsetType);
        }
    }

    public void setSprite(Image newSprite) {
        if (sprite != newSprite) {
            sprite = newSprite;
        }
    }

    public void setCollider(int newWidth, int newHeight) {
        int dw = rect.w - newWidth;
        int dh = rect.h - newHeight;

        rect.pos.x += dw;
        rect.pos.y += dh;
        rect.w = newWidth;
        rect.h = newHeight;
    }

    public void accelerate() {
        float yAcceleration = gravity ? Config.GRAVITY : 0;
        vel.add(new Vector2(acceleration, yAcceleration).multiply(Time.deltaTime));
        if (maxVel != 0) {
            vel.x = Utils.clamp(vel.x, -maxVel, maxVel);
        }
        vel.x *= friction;
    }

    public void onCollision (GameObject gameObject, float dx, float dy) {};

    public void update(){}
}