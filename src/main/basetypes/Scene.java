package src.main.basetypes;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import src.main.objects.Camera;
import src.main.Config;
import src.main.Main;
import src.main.globals.Time;

public class Scene {
    public String nextScene;
    static public Camera camera;
    public BufferedImage background;
    public ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
    public boolean hasCamera = false;

    public Scene(boolean hasCamera) {
        this.hasCamera = hasCamera;
    }

    public void update() {
        updateGameObjects();
    }

    // ______ PHYSICS ______

    public void physicsUpdate() {
        for (GameObject gameObject : gameObjects) {
            if(gameObject.isActive) {
                gameObject.accelerate();
                moveGameObject(gameObject);
            }

            // If the gameObject is mario, move the camera
            if (gameObject.tag.equals(Config.MARIO_TAG)) {
                camera.updatePosition(gameObject.rect.pos, gameObject.vel);
                preventBackTrack(gameObject); // Prevent player from backtracking
            }
        }
    };

    public void moveGameObject(GameObject gameObject) {
        if (gameObject.vel.x != 0) {
            moveSingleAxis(gameObject, gameObject.vel.x, 0);
        } 
        if (gameObject.vel.y != 0) {
            moveSingleAxis(gameObject, 0, gameObject.vel.y);
        } 
    }
    
    public void moveSingleAxis(GameObject gameObject, float dx, float dy) {
        gameObject.rect.pos.x += dx * Time.deltaTime;
        gameObject.rect.pos.y += dy * Time.deltaTime;

        handleCollisions(gameObject, dx, dy);
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
                // Ignore collisions with itself
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
        renderGameObjects(g2d);
    }

    public void updateGameObjects() {
        for (GameObject gameObject : gameObjects) {
            if (camera.contains(gameObject.rect) && !gameObject.isActivated) {
                gameObject.isActive = true;
                gameObject.isActivated = true;
            }
            if (gameObject.isActive) {
                gameObject.update();
            }
        }
    }

    public void renderBackground(Graphics2D g2d) {
        if(hasCamera) {
            g2d.drawImage(background, (int) -camera.pos.x, (int) -camera.pos.y, Main.canvas);
        } else {
            g2d.drawImage(background, 0, 0, Main.canvas);
        }
    }

    public void renderGameObjects(Graphics2D g2d) {
        
        for (GameObject gameObject : gameObjects) {
            Vector2 relativePosition = camera.toViewspace(gameObject.rect.pos);

            if (gameObject.isActive) {
                if (gameObject.flipSprite) {

                    g2d.drawImage(
                        gameObject.sprite,
                        (int) relativePosition.x + gameObject.sprite.getWidth(null),
                        (int) relativePosition.y,
                        -gameObject.sprite.getWidth(null),
                        gameObject.sprite.getHeight(null),
                        Main.canvas
                    );

                } else {

                    g2d.drawImage(
                        gameObject.sprite,
                        (int) relativePosition.x,
                        (int) relativePosition.y,
                        Main.canvas
                    );

                }
            }
        }
    }

    public void debugColliders(Graphics2D g2d, Color color) {
        g2d.setColor(color);
        for (GameObject col : gameObjects) {
            if (col.isActive) {
                g2d.drawRect((int) (col.rect.pos.x - camera.pos.x), (int) col.rect.pos.y, col.rect.w, col.rect.h);
            }
        }
    }
}