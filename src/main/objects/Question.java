package src.main.objects;

import src.main.basetypes.GameObject;
import src.main.basetypes.Rectangle;
import src.main.basetypes.State;
import src.main.basetypes.StateMachine;
import src.main.globals.SpriteAtlas;
import src.main.globals.Time;
import src.main.Config;

public class Question extends GameObject {
    private States states = new States();
    private StateMachine stateMachine;
    private Animation animation;
    private GameObject item;

    public Question(Rectangle rect, GameObject item) {
        super(Config.QUESTION_TAG, SpriteAtlas.question[1], rect);
        this.item = item;
        animation = new Animation();
        stateMachine = new StateMachine(states.new IdleState());
        enteredViewSpace = true; // For synchronized animations
    }

    public void onCollision(GameObject col, float dx, float dy) {
        if (col.hasTag(Config.MARIO_TAG)) {
            if (dy < 0) {
                stateMachine.onEvent(Events.bounce);
            }
        }
    }

    public State getState() {
        return stateMachine.getState();
    }

    private class Animation {
        private float animTimer = 0;
        private int animFrame = 0;
        private int startHeight;
        private int[] idleFrames = {1, 2, 3, 2, 1};

        public Animation() {
            this.startHeight = (int) rect.pos.y;
        }

        public void resetAnim() {
            animTimer = 0;
            animFrame = 0;
        }

        public void bounceAnim() {
            animFrame += 1;
            rect.pos.y = startHeight - bounceAnimFunction(animFrame);
            if(animFrame == 12) {
                animFrame = 0;
            }
        }

        private int bounceAnimFunction(int frame) {
            return -Math.abs(frame * 4 - 24) + 24;
        }

        public void idleAnim() {
            animTimer += Time.deltaTime;
            if (animTimer > 7 * Time.deltaTime * animFrame) {
                if (animTimer > 7 * 7 * Time.deltaTime && animFrame >= 5) {
                    animTimer = 0;
                    animFrame = 0;
                }
                
                if (animFrame < 5) {
                    setSprite(SpriteAtlas.question[idleFrames[animFrame]]);
                    animFrame++;
                }
            }
        }
    }

    public void update() {
        stateMachine.update();
    }

    private interface Events {
        String bounce = "bounce";
        String open = "open";
    }

    public class States implements Events {
        public class IdleState extends State {

            public State onEvent(String event) {
                if (event.equals(bounce)) {
                    return new BounceState();
                }

                return this;
            }

            public void update() {
                animation.idleAnim();
            }

            public void onExit() {
                animation.resetAnim();
            }
        }

        public class BounceState extends State {
            public State onEvent(String event) {
                if (event.equals(open)) {
                    return new OpenState();
                }

                return this;
            }

            public void onEnter(String event) {
                sprite = SpriteAtlas.question[0];
                isEntity = true;

                if (item.hasTag(Config.COIN_TAG)) {
                    item.setActive(true);
                }
            }

            public void update() {
                animation.bounceAnim();
                if (animation.animFrame == 0) {
                    stateMachine.onEvent(Events.open);
                }
            }

            public void onExit() {
                isEntity = false;
            }
        }

        public class OpenState extends State {
            public void onEnter(String event) {
                if (item.hasTag(Config.SUPER_MUSHROOM_TAG)) {
                    item.setActive(true);
                }
            }
        }
    }
}
