package src.main.basetypes;

import java.awt.*;

import src.main.Config;
import src.main.globals.Time;
import src.main.utils.Utils;

public class GameObject {
    public String tag;

    public Image sprite;

    public Rectangle rect;
    public Vector2 vel = new Vector2(0, 0);
    
    public boolean freezeMovement = false;
    public boolean gravity = false;
    public boolean isActivated = false; // Has entered the camera viewspace at some point
    public boolean isAwake = true; // True by default, can be turned to false for custom behavior
    public boolean hasCollider = true;
    public boolean isEntity = false;
    public boolean flipSprite = false;
    
    public float friction = 1;
    public float acceleration = 0;
    public float maxVel = 0;

    private String triggeredScene; // GameObjects can trigger a scene change depending on events
    public boolean hasTriggeredScene;

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

    public void accelerate() {
        float yAcceleration = this.gravity ? Config.GRAVITY : 0;
        vel.add(new Vector2(acceleration, yAcceleration).multiply(Time.deltaTime));
        vel.x = Utils.clamp(vel.x, -maxVel, maxVel);
        vel.x *= friction;
    }

    public void onCollision (GameObject gameObject, float dx, float dy) {};

    public void update(){}
}