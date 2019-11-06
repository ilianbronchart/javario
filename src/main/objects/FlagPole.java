package src.main.objects;

import javafx.scene.image.Image;
import src.main.basetypes.GameObject;
import src.main.basetypes.Rectangle;
import src.main.globals.SpriteAtlas;
import src.main.Config;

public class FlagPole extends GameObject {
    private boolean animateFlag = false;

    public FlagPole(Rectangle rect) {
        super(Config.FLAGPOLE_TAG, null, rect);
        hasCollider = true;
        isEntity = true;
        animateFlag = false;
        childGameObjects.add(new Flag(new Rectangle(rect.pos.x - 24, rect.pos.y + 24, 48, 48)));
    }

    public void onCollision(GameObject other, float dx, float dy) {
        if (other.hasTag(Config.MARIO_TAG)) {
            animateFlag = true;
            if (other.rect.pos.y + other.rect.h > rect.pos.y + rect.h - 20) {
                hasCollider = false;
                isAwake = false;
            }
        }
    }

    public class Flag extends GameObject {
        public Flag(Rectangle rect) {
            super("", SpriteAtlas.flag, rect);
            hasCollider = false;
        }

        public void update() {
            if (animateFlag && this.rect.pos.y + this.rect.h < FlagPole.this.rect.pos.y + FlagPole.this.rect.h) {
                rect.pos.y += 3f;
            }
        }
    }
}