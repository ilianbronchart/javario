package src.main.objects;

import src.main.Config;
import src.main.basetypes.GameObject;
import src.main.basetypes.Rectangle;
import src.main.globals.SpriteAtlas;
import src.main.globals.Time;

public class Coin extends GameObject {
    private Animation animation;
    private boolean addedCoinScore = false;

    public Coin(Rectangle rect) {
        super(Config.COIN_TAG, SpriteAtlas.coin[0], rect);
        setActive(false);
        hasCollider = false;
        animation = new Animation();
    }

    public void update() {
        animation.animate();
        if (rect.pos.y >= animation.startHeight) {
            setActive(false);
        }
        
        if (!addedCoinScore) {
            ScoreSystem.addCoin();
        }
        addedCoinScore = true;
    }

    private class Animation {
        private int startHeight;
        private int animFrame = 0;
        private float animTimer = 0;
        private float bounceHeight = 0;

        private Animation() {
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

        private float animFunction(float bounceHeight) {
            return (float) -Math.pow((bounceHeight - 12), 2) + 144;
        }
    }
}
