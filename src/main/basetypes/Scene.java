package src.main.basetypes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import src.main.objects.Camera;
import src.main.Config;
import src.main.Main;
import src.main.globals.Time;

public class Scene {
    public String nextScene; // The scene that is to be queued after the current one
    static public Camera camera;
    public BufferedImage background;
    public ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();

    public Scene(int x, int y, int w, int h, int maxScroll) {
        camera = new Camera(x, y, w, h, maxScroll);
    }

    public void update() {
        updateGameObjects(gameObjects);
        physicsUpdate(gameObjects);
    }

    public void updateGameObjects(ArrayList<GameObject> objects) {
        for (GameObject obj : objects) {
            if (camera.contains(obj.rect)) {
                obj.isActivated = true;
            }
            if (obj.isAwake && obj.isActivated) {
                obj.update();
            }

            if (obj.hasTriggeredScene) {
                nextScene = obj.getTriggeredScene();
                break;
            }

            // Recursively update child GameObjects
            updateGameObjects(obj.childGameObjects);
        }
    }

    public void physicsUpdate(ArrayList<GameObject> objects) {
        for (GameObject obj : objects) {
            if(obj.isAwake && obj.isActivated && !obj.freezeMovement) {
                obj.accelerate();
                moveGameObject(obj);

                // If the GameObject is mario, move the camera
                if (obj.tag.equals(Config.MARIO_TAG)) {
                    camera.updatePosition(obj.rect.pos, obj.vel);
                    preventBackTrack(obj); // Prevent player from backtracking
                }
            }

            // Recursively update child GameObjects
            physicsUpdate(obj.childGameObjects);
        }
    };

    // ______ PHYSICS ______

    public void moveGameObject(GameObject obj) {
        if (obj.vel.x != 0) {
            moveSingleAxis(obj, obj.vel.x, 0);
        } 
        if (obj.vel.y != 0) {
            moveSingleAxis(obj, 0, obj.vel.y);
        } 
    }
    
    public void moveSingleAxis(GameObject obj, float dx, float dy) {
        obj.rect.pos.x += dx * Time.deltaTime;
        obj.rect.pos.y += dy * Time.deltaTime;

        if (obj.hasCollider) {
            handleCollisions(obj, dx, dy);
        }
    }

    public void preventBackTrack(GameObject mario) {
        if (mario.rect.pos.x < camera.pos.x) {
            mario.rect.pos.x = camera.pos.x;
            mario.vel.x = 0;
            mario.acceleration = 0;
        }
    }

    // ______ COLLISION METHODS ______

    public void handleCollisions(GameObject col, float dx, float dy) {
        ArrayList<GameObject> others = getCollisions(col, gameObjects);

        for (GameObject other : others) {
            handleSingleCollision(col, other, dx, dy);
        }
    }

    public void handleSingleCollision(GameObject col, GameObject other, float dx, float dy) {
        if (other.isEntity) {
            // Run specific collision events between entities
            col.onCollision(other, dx, dy);
            other.onCollision(col, dx, dy);
            return;
        }


        // Handle collisions with static colliders
        if (dx > 0) {
            col.rect.pos.x = other.rect.pos.x - col.rect.w;
        } else if (dx < 0) {
            col.rect.pos.x = other.rect.pos.x + other.rect.w;
        } else if (dy > 0) {
            col.rect.pos.y = other.rect.pos.y - col.rect.h;
        } else if (dy < 0) {
            col.rect.pos.y = other.rect.pos.y + other.rect.h;
        }

        col.onCollision(other, dx, dy);
        other.onCollision(col, dx, dy);
    }

    public ArrayList<GameObject> getCollisions(GameObject col, ArrayList<GameObject> colliderList) {
        ArrayList<GameObject> colliders = new ArrayList<GameObject>();

        for(GameObject collider : colliderList) {
            if (!collider.hasCollider || collider == col) {
                continue;
            }

            if (col.rect.pos.x - collider.rect.pos.x < 100 || collider.rect.w > 100) {
                if (col.rect.overlaps(collider.rect)) {
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

    public void renderBackground(Graphics2D g2d) {
        g2d.drawImage(background, (int) -camera.pos.x, (int) -camera.pos.y, Main.canvas);
    }

    public void renderGameObjects(Graphics2D g2d, ArrayList<GameObject> objects) {
        for (GameObject obj : objects) {
            renderGameObject(g2d, obj);

            // Recursively render child GameObjects
            renderGameObjects(g2d, obj.childGameObjects);
        }
    }

    public void renderGameObject(Graphics2D g2d, GameObject obj) {
        Vector2 spritePosition = obj.rect.pos.getAdd(obj.spriteOffset);
        Vector2 relativePosition = camera.toViewspace(spritePosition);

        if (obj.isAwake && obj.isActivated) {
            if (obj.flipSprite) {

                g2d.drawImage(
                    obj.sprite,
                    (int) relativePosition.x + obj.sprite.getWidth(null),
                    (int) relativePosition.y,
                    -obj.sprite.getWidth(null),
                    obj.sprite.getHeight(null),
                    Main.canvas
                );

            } else {

                g2d.drawImage(
                    obj.sprite,
                    (int) relativePosition.x,
                    (int) relativePosition.y,
                    Main.canvas
                );

            }
        }
    }

    public void debugColliders(Graphics2D g2d, Color color) {
        g2d.setColor(color);
        for (GameObject col : gameObjects) {
            if (col.isAwake && col.isActivated && col.hasCollider) {
                g2d.drawRect((int) (col.rect.pos.x - camera.pos.x), (int) col.rect.pos.y, col.rect.w, col.rect.h);
            }
        }
    }
}