package src.main.objects;

import src.main.basetypes.GameObject;
import src.main.basetypes.Rectangle;
import src.main.basetypes.State;
import src.main.basetypes.StateMachine;
import src.main.globals.SpriteAtlas;
import src.main.globals.Time;
import src.main.Config;

public class Question extends GameObject {
    States states = new States();
    StateMachine stateMachine;
    Animation animation;
    GameObject item;

    public Question(Rectangle rect, GameObject item) {
        super(Config.QUESTION_TAG, SpriteAtlas.question[1], rect);
        this.item = item;
        animation = new Animation();
        stateMachine = new StateMachine(states.new IdleState());
        isActivated = true; // For synchronized animations
    }

    public void onCollision(GameObject col, float dx, float dy) {
        if (col.tag.equals(Config.MARIO_TAG)) {
            if (dy < 0) {
                stateMachine.onEvent(Events.bounce);
            }
        }
    }

    class Animation {
        float animTimer = 0;
        int animFrame = 0;
        int startHeight;
        int[] idleFrames = {1, 2, 3, 2, 1};
        boolean finishedAnimating = false;

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

        public int bounceAnimFunction(int frame) {
            return -Math.abs(frame * 4 - 24) + 24;
        }

        public void idleAnim() {
            animTimer += Time.deltaTime;
            if (animTimer > 6 * Time.deltaTime) {
                finishedAnimating = false;
                sprite = SpriteAtlas.question[idleFrames[animFrame]];
                animFrame++;
                animTimer = 0;
            }

            // Animation is on the last frame
            if (animFrame == 5) {
                finishedAnimating = true;
                animFrame = 0;
            }
        }
    }

    public void update() {
        stateMachine.update();
    }

    interface Events {
        String bounce = "bounce";
        String open = "open";
    }

    public class States implements Events {
        public class IdleState extends State {
            float doAnimationTimer;

            public State onEvent(String event) {
                if (event.equals(bounce)) {
                    return new BounceState();
                }

                return this;
            }

            public void update() {
                doAnimationTimer += Time.deltaTime;
                if (doAnimationTimer > 5 * Time.deltaTime) {
                    animation.idleAnim();
                    if (animation.finishedAnimating) {
                        doAnimationTimer = 0;
                    }
                }
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
                item.isAwake = true;
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

        public class OpenState extends State {}
    }
}
