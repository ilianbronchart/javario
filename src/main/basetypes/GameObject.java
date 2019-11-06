package src.main.basetypes;

import java.awt.*;
import java.util.ArrayList;

import src.main.Config;
import src.main.globals.Time;
import src.main.utils.Utils;

public class GameObject {
    private String tag;
    private String triggeredScene; // GameObjects can trigger a scene change depending on events
    private boolean hasTriggeredScene;

    protected Image sprite;
    protected Rectangle rect;
    protected Vector2 vel = new Vector2();
    protected Vector2 spriteOffset = new Vector2(); // Offset between sprite and collider
    protected boolean gravity = false;
    protected boolean isActive = true; // True by default, can be turned to false for custom behavior
    protected boolean freezeMovement = false;
    protected boolean enteredViewSpace = false; // Has entered the camera viewspace at some point
    protected boolean hasCollider = true;
    protected boolean isEntity = false; // Indicates whether the GameObject should interact with other entities
    protected boolean flipSprite = false;
    protected boolean fixedRender = false; // Render this object at a fixed position on screen (don't scroll)
    protected boolean hasSpecializedRendering = false; // Some GameObjects might want to render their own subcomponents
    protected float friction = 1;
    protected float acceleration = 0;
    protected float maxVel = 0;

    protected ArrayList<GameObject> childGameObjects = new ArrayList<GameObject>();
    
    public GameObject(String tag, Image sprite, Rectangle rect){
        this.tag = tag == null ? "" : tag;
        this.sprite = sprite;
        this.rect = rect;
    }
    
    public boolean hasTag(String tag) {
        return this.tag.equals(tag);
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

    protected void setSprite(Image newSprite) {
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

    public void render(Graphics2D g2d){}

    // _______________________ GETTERS AND SETTERS _______________________

    
    public void triggerScene(String scene) {
        hasTriggeredScene = true;
        triggeredScene = scene;
    }
    
    public String getTriggeredScene() { return triggeredScene; }
    public boolean isActive() { return isActive; }
    public void setActive(boolean value) { isActive = value; }
    public boolean isEntity() { return isEntity; }
    public boolean hasTriggeredScene() { return hasTriggeredScene; }
    public ArrayList<GameObject> getChildGameObjects() { return childGameObjects; }
    public Image getSprite() { return sprite; }
    public Vector2 getSpriteOffset() { return spriteOffset; }
    public Rectangle rect() { return rect; }
    public Vector2 vel() { return vel; }
    public boolean enteredViewSpace() { return enteredViewSpace; }
    public void setEnteredViewSpace(boolean value) { enteredViewSpace = value; };
}