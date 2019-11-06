package src.main.basetypes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Vector;

import src.main.objects.Camera;
import src.main.objects.ScoreSystem;
import src.main.Config;
import src.main.Main;
import src.main.globals.Time;
import src.main.basetypes.Vector2;

public class Scene {
    private String nextScene; // The scene that is to be queued after the current one
    static protected Camera camera;
    protected BufferedImage background;
    protected BufferedImage foreground;
    protected Vector2 foregroundPos;
    protected ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();

    public Scene(Vector2 startPos, int w, int h, int maxScroll) {
        camera = new Camera(startPos, w, h, maxScroll);
    }

    public void update() {
        updateGameObjects(gameObjects);
        physicsUpdate(gameObjects);
    }

    public String getNextScene() {
        return nextScene;
    }

    protected void updateGameObjects(ArrayList<GameObject> objects) {
        for (GameObject obj : objects) {
            if (camera.contains(obj.rect)) {
                obj.setEnteredViewSpace(true);
            }
            if (obj.isActive() && obj.enteredViewSpace()) {
                obj.update();
            }

            if (obj.hasTriggeredScene()) {
                nextScene = obj.getTriggeredScene();
                break;
            }

            // Recursively update child GameObjects
            updateGameObjects(obj.getChildGameObjects());
        }
    }

    // ______ PHYSICS ______
    
    protected void physicsUpdate(ArrayList<GameObject> objects) {
        for (GameObject obj : objects) {
            if(obj.isActive() && obj.enteredViewSpace() && !obj.freezeMovement) {
                obj.accelerate();
                moveGameObject(obj);

                // If the GameObject is mario, move the camera
                if (obj.hasTag(Config.MARIO_TAG)) {
                    camera.updatePosition(obj.rect.pos, obj.vel());
                    preventBackTrack(obj); // Prevent player from backtracking
                }
            }

            // Recursively update child GameObjects
            physicsUpdate(obj.getChildGameObjects());
        }
    };
    
    private void moveGameObject(GameObject obj) {
        if (obj.vel().x != 0) {
            moveSingleAxis(obj, obj.vel().x, 0);
        } 
        if (obj.vel().y != 0) {
            moveSingleAxis(obj, 0, obj.vel().y);
        } 
    }
    
    private void moveSingleAxis(GameObject obj, float dx, float dy) {
        obj.rect.pos.x += dx * Time.deltaTime;
        obj.rect.pos.y += dy * Time.deltaTime;

        if (obj.hasCollider) {
            handleCollisions(obj, dx, dy);
        }
    }

    private void preventBackTrack(GameObject mario) {
        if (mario.rect().pos.x < camera.pos.x) {
            mario.rect().pos.x = camera.pos.x;
            mario.vel().x = 0;
            mario.acceleration = 0;
        }
    }

    // ______ COLLISION METHODS ______

    private void handleCollisions(GameObject col, float dx, float dy) {
        ArrayList<GameObject> others = getCollisions(col, gameObjects);

        for (GameObject other : others) {
            handleSingleCollision(col, other, dx, dy);
        }
    }

    private void handleSingleCollision(GameObject col, GameObject other, float dx, float dy) {
        if (other.isEntity()) {
            // Run specific collision events between entities
            col.onCollision(other, dx, dy);
            other.onCollision(col, dx, dy);
            return;
        }


        // Handle collisions with static colliders
        if (dx > 0) {
            col.rect().pos.x = other.rect().pos.x - col.rect().w;
        } else if (dx < 0) {
            col.rect().pos.x = other.rect().pos.x + other.rect().w;
        } else if (dy > 0) {
            col.rect().pos.y = other.rect().pos.y - col.rect().h;
        } else if (dy < 0) {
            col.rect().pos.y = other.rect().pos.y + other.rect().h;
        }

        col.onCollision(other, dx, dy);
        other.onCollision(col, dx, dy);
    }

    private ArrayList<GameObject> getCollisions(GameObject col, ArrayList<GameObject> colliderList) {
        ArrayList<GameObject> colliders = new ArrayList<GameObject>();

        for(GameObject collider : colliderList) {
            if (!collider.hasCollider || collider == col) {
                continue;
            }

            if (col.rect().pos.x - collider.rect.pos.x < 100 || collider.rect.w > 100) {
                if (col.rect().overlaps(collider.rect)) {
                    colliders.add(collider);
                }
            }
        }
        
        return colliders;
    }

    // ______ RENDERING METHODS ______ 

    public void render(Graphics2D g2d) {
        renderBackground(g2d);
        renderGameObjects(g2d, gameObjects);
    }

    protected void renderBackground(Graphics2D g2d) {
        g2d.drawImage(background, (int) -camera.pos.x, (int) -camera.pos.y, Main.canvas);
    }
    
    protected void renderForeground(Graphics2D g2d) {
        Vector2 relativePosition = camera.toViewspace(foregroundPos);
        g2d.drawImage(foreground, (int) relativePosition.x, (int) relativePosition.y, Main.canvas);
    }

    protected void renderGameObjects(Graphics2D g2d, ArrayList<GameObject> objects) {
        for (GameObject obj : objects) {
            renderGameObject(g2d, obj);

            if (obj.hasSpecializedRendering) {
                obj.render(g2d);
            }

            // Recursively render child GameObjects
            renderGameObjects(g2d, obj.getChildGameObjects());
        }
    }

    private void renderGameObject(Graphics2D g2d, GameObject obj) {
        Vector2 spritePosition = obj.rect.pos.getAdd(obj.getSpriteOffset());
        Vector2 relativePosition = camera.toViewspace(spritePosition);

        if (obj.fixedRender) {
            relativePosition = spritePosition;
        }

        if (obj.isActive() && obj.enteredViewSpace()) {
            if (obj.flipSprite) {
                g2d.drawImage(
                    obj.getSprite(),
                    (int) relativePosition.x + obj.getSprite().getWidth(null),
                    (int) relativePosition.y,
                    -obj.getSprite().getWidth(null),
                    obj.getSprite().getHeight(null),
                    Main.canvas
                );
            } else {
                g2d.drawImage(
                    obj.getSprite(),
                    (int) relativePosition.x,
                    (int) relativePosition.y,
                    Main.canvas
                );
            }
        }
    }

    protected void debugColliders(Graphics2D g2d, Color color) {
        g2d.setColor(color);
        for (GameObject col : gameObjects) {
            if (col.isActive() && col.enteredViewSpace() && col.hasCollider) {
                g2d.drawRect((int) (col.rect().pos.x - camera.pos.x), (int) col.rect().pos.y, col.rect().w, col.rect().h);
            }
        }
    }
}