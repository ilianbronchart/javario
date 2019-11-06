package src.main.objects;

import src.main.Config;
import src.main.basetypes.GameObject;
import src.main.basetypes.Rectangle;
import src.main.globals.SpriteAtlas;

public class SuperMushroom extends GameObject {
    Animation animation;

    public SuperMushroom(Rectangle rect) {
        super(Config.SUPER_MUSHROOM_TAG, SpriteAtlas.superMushroom, rect);
        isEntity = false;
        isAwake = false;
        hasCollider = false;
        animation = new Animation();
    }
    
    public void update() {
        if (!animation.doneAnimating) {
            animation.deployAnim();
            if (animation.doneAnimating) {
                isEntity = true;
                hasCollider = true;
                gravity = true;
                vel.x = Config.ENTITY_START_VEL_X;
            }
        }
    }

    public void onCollision(GameObject other, float dx , float dy) {
        if (other.tag.equals(Config.MARIO_TAG)) {
            isAwake = false;
            hasCollider = false;
        } else {
            if (dy > 0) {
                vel.y = 0;
            } else if (dx != 0) {
                vel.x = -vel.x;
                if (dx > 0) {
                    rect.pos.x = other.rect.pos.x - rect.w;  
                } else {
                    rect.pos.x = other.rect.pos.x + other.rect.w;  
                }
            }
        }
    }

    class Animation {
        int startHeight;
        boolean doneAnimating = false;

        public Animation() {
            startHeight = (int) rect.pos.y;
        }

        public void deployAnim() {
            if (startHeight - rect.pos.y == 48) {
                doneAnimating = true;
            }
            rect.pos.y -= 1;
        }
    }
}
