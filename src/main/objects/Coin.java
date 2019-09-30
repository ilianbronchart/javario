package src.main.objects;

import src.main.Config;
import src.main.basetypes.GameObject;
import src.main.basetypes.Rectangle;
import src.main.globals.SpriteAtlas;
import src.main.globals.Time;

public class Coin extends GameObject {
    Animation animation;

    public Coin(Rectangle rect) {
        super(Config.COIN_TAG, SpriteAtlas.coin[0], rect);
        isAwake = false;
        hasCollider = false;
        animation = new Animation();
    }

    public void update() {
        animation.animate();
        if (rect.pos.y >= animation.startHeight) {
            isAwake = false;
        }
    }

    class Animation {
        int startHeight;
        int animFrame = 0;
        float animTimer = 0;
        float bounceHeight = 0;

        public Animation() {
            startHeight = (int) rect.pos.y;
        }

        public void animate() {
            animTimer += Time.deltaTime;
            bounceHeight += Config.COIN_BOUNCE_SPEED;
            if (animTimer > 3 * Time.deltaTime) {
                animTimer = 0;
                animFrame += 1;
                sprite = SpriteAtlas.coin[animFrame % 4];
            }
            rect.pos.y = startHeight - animFunction(bounceHeight);
        }

        public float animFunction(float bounceHeight) {
            return (float) -Math.pow((bounceHeight - 12), 2) + 144;
        }
    }
}
