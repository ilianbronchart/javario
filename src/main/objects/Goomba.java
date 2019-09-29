package src.main.objects;

import src.main.Config;
import src.main.basetypes.GameObject;
import src.main.basetypes.Rectangle;
import src.main.basetypes.State;
import src.main.basetypes.StateMachine;
import src.main.globals.SpriteAtlas;
import src.main.globals.Time;

public class Goomba extends GameObject {
    Animation animation;
    StateMachine stateMachine;
    States states = new States();

    float squishTimer = 0;

    public Goomba(Rectangle rect) {
        super(Config.GOOMBA_TAG, SpriteAtlas.goomba[0], rect);
        gravity = true;
        isEntity = true;
        animation = new Animation();
        stateMachine = new StateMachine(states.new RunState());
        maxVel = Config.ENEMY_START_VEL_X;
        vel.x = -Config.ENEMY_START_VEL_X;
    }

    public void update() {
        if (squishTimer > 20 * Time.deltaTime) {
            stateMachine.onEvent(Events.dead);       
        }
        stateMachine.update();
    }

    public void onCollision (GameObject col, float dx, float dy) {
        if(col.tag.equals(Config.MARIO_TAG)) {
            if (col.rect.pos.y + col.rect.h - dy * Time.deltaTime < rect.pos.y) {
                // Mario is squishing this goomba
                stateMachine.onEvent(Events.squish);
            }
        } else {
            if (dy > 0) {
                vel.y = 0;
            } else if (dx != 0) {
                vel.x = -vel.x;
            }
        }
    }

    public class Animation {
        float animTimer = 0;
        int animFrame = 0;

        public void runAnim() {
            sprite = SpriteAtlas.goomba[animFrame % 2];
            animTimer += Time.deltaTime;
            if (animTimer > 14 * Time.deltaTime) {
                animFrame += 1;
                animTimer = 0;
            }
        }
    }

    public static interface Events {
        static String knocked = "knocked";
        static String squish = "squish";
        static String dead = "dead";
    }

    public class States implements Events {
        public class RunState extends State {
            public State onEvent(String event) {
                switch (event) {
                    case knocked:
                        return new KnockedState();
                    case squish:
                        return new SquishState();
                }

                return this;
            }

            public void update() {
                animation.runAnim();
            }
        }

        public class KnockedState extends State {
            public State onEvent(String event) {
                if(event.equals(dead)) {
                    return new DeadState();
                }

                return this;
            }

            public void onEnter(String event) {
                vel.y = Config.GOOMBA_KNOCKED_VEL;
                sprite = SpriteAtlas.goomba[3];
                // TODO: totalScore += goomba score
                // TODO: sounds.kick.play();
            }
        }

        public class SquishState extends State {
            public State onEvent(String event) {
                if(event.equals(dead)) {
                    return new DeadState();
                }
                return this;
            }

            public void onEnter(String event) {
                sprite = SpriteAtlas.goomba[2];
                squishTimer += Time.deltaTime;
                vel.x = 0;
                hasCollider = false;
            }
            
            public void update() {
                squishTimer += Time.deltaTime;
            }
        }

        public class DeadState extends State {
            public void onEnter(String event) {
                isActive = false;
            }
        }

    }
}
