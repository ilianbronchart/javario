package src.main.objects;

import src.main.Config;
import src.main.basetypes.GameObject;
import src.main.basetypes.Rectangle;
import src.main.globals.SpriteAtlas;

public class SuperMushroom extends GameObject {
    private Animation animation;
    private boolean isDeployed = false;

    public SuperMushroom(Rectangle rect) {
        super(Config.SUPER_MUSHROOM_TAG, SpriteAtlas.superMushroom, rect);
        isEntity = false;
        setActive(false);
        hasCollider = false;
        animation = new Animation();
    }
    
    public void update() {
        if (!isDeployed) {
            animation.deployAnim();

            if (isDeployed) {
                isEntity = true;
                hasCollider = true;
                gravity = true;
                vel.x = Config.ENTITY_START_VEL_X;
            }
        }
    }

    public void onCollision(GameObject other, float dx , float dy) {
        if (other.hasTag(Config.MARIO_TAG)) {
            setActive(false);
            hasCollider = false;
            ScoreSystem.addScore(Config.MUSHROOM_SCORE);

            return;
        } 
        
        if (other.isEntity()) {
            if (other.hasTag(Config.BRICK_TAG) || other.hasTag(Config.QUESTION_TAG)) {
                vel.y -= 0.3f;
            }
            
            // Cancel collision
            return;
        }

        if (dy > 0) {
            vel.y = 0;
        } else if (dx != 0) {
            vel.x = -vel.x;

            if (dx > 0) {
                rect.pos.x = other.rect().pos.x - rect.w;  
            } else {
                rect.pos.x = other.rect().pos.x + other.rect().w;  
            }
        }
    }

    private class Animation {
        private int startHeight;

        public Animation() {
            startHeight = (int) rect.pos.y;
        }

        public void deployAnim() {
            if (startHeight - rect.pos.y == 48) {
                isDeployed = true;
            }
            rect.pos.y -= 1;
        }
    }
}
