package src.main.objects;

import src.main.basetypes.Rectangle;
import src.main.basetypes.State;
import src.main.basetypes.StateMachine;
import src.main.basetypes.Vector2;
import src.main.basetypes.GameObject;

import src.main.Config;
import src.main.globals.SpriteAtlas;
import src.main.globals.Time;

public class Brick extends GameObject {
    private GameObject item;
    private States states = new States();
    private Animation animation;
    private StateMachine stateMachine;

    public Brick(Rectangle rect, GameObject item) {
        super(Config.BRICK_TAG, SpriteAtlas.brick, rect);
        animation = new Animation();
        stateMachine = new StateMachine(states.new IdleState());
        this.item = item;
    }

    public void update() {
        stateMachine.update();
    }

    public void onCollision(GameObject col, float dx, float dy) {
        if (col.hasTag(Config.MARIO_TAG)) {
            if (dy < 0) {
                Mario mario = (Mario) col;

                if(mario.getMarioState() instanceof Mario.States.SmallMario) {
                    stateMachine.onEvent(Events.bounce);
                } else {
                    stateMachine.onEvent(Events.smash);
                }
            }
        }
    }

    private class Animation {
        private int animFrame = 0;
        private int startHeight;

        private Animation() {
            this.startHeight = (int) rect.pos.y;
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
    }

    public State getState() {
        return stateMachine.getState();
    }

    public interface Events {
        String bounce = "bounce";
        String smash = "smash";
        String idle = "idle";
    }

    public class States implements Events {

        public class IdleState extends State {
            public State onEvent(String event) {
                switch (event) {
                    case bounce:
                        return new BounceState();
                    case smash:
                        return new SmashState();
                }

                return this;
            }

            public void onEnter(String event) {
                isEntity = false;
            }
        }

        public class BounceState extends State {
            public State onEvent(String event) {
                if (event.equals(idle)) {
                    return new IdleState();
                }

                return this;
            }

            public void onEnter(String event) {
                isEntity = true;
                item.setActive(true);
            }

            public void update() {
                animation.bounceAnim();
                if (animation.animFrame == 0) {
                    stateMachine.onEvent(Events.idle);
                }
            }
        }

        public class SmashState extends State {
            public void onEnter(String state) {
                childGameObjects.add(new BrickFragment(new Vector2(rect.pos.x, rect.pos.y), new Vector2(-0.1f, -0.5f)));
                childGameObjects.add(new BrickFragment(new Vector2(rect.pos.x + 24, rect.pos.y), new Vector2(0.1f, -0.5f)));
                childGameObjects.add(new BrickFragment(new Vector2(rect.pos.x, rect.pos.y + 24), new Vector2(-0.1f, -0.4f)));
                childGameObjects.add(new BrickFragment(new Vector2(rect.pos.x + 24, rect.pos.y + 24), new Vector2(0.1f, -0.4f)));

                hasCollider = false;
                setActive(false);
            }
        }
    }

    private class BrickFragment extends GameObject {
        private int animFrame = 0;
        private float animTimer = 0;

        public BrickFragment(Vector2 startPos, Vector2 startVel) {
            super(Config.BRICK_FRAGMENT_TAG, SpriteAtlas.brickFragments[0], new Rectangle(startPos.x, startPos.y, 0, 0));
            vel = startVel;
            hasCollider = false;
            gravity = true;
            setActive(true);
        }

        public void update() {
            if (animTimer > 7 * Time.deltaTime * animFrame) {
                setSprite(SpriteAtlas.brickFragments[animFrame % 2]);
                animFrame++;
            }
            animTimer += Time.deltaTime;

            if (rect.pos.y > Config.FRAME_SIZE[1]) {
                setActive(false);
            }
        }
    }
}