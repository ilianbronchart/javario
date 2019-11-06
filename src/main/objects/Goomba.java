package src.main.objects;

import src.main.Config;
import src.main.basetypes.GameObject;
import src.main.basetypes.Rectangle;
import src.main.basetypes.State;
import src.main.basetypes.StateMachine;
import src.main.globals.SpriteAtlas;
import src.main.globals.Time;

public class Goomba extends GameObject {
    private Animation animation;
    private StateMachine stateMachine;
    private States states = new States();
    private float squishTimer = 0;

    public Goomba(Rectangle rect) {
        super(Config.GOOMBA_TAG, SpriteAtlas.goomba[0], rect);
        gravity = true;
        isEntity = true;
        animation = new Animation();
        stateMachine = new StateMachine(states.new RunState());
        vel.x = -Config.ENTITY_START_VEL_X;
    }

    public void update() {
        if (squishTimer > 20 * Time.deltaTime) {
            stateMachine.onEvent(Events.dead);       
        }
        stateMachine.update();
    }

    public void onCollision (GameObject col, float dx, float dy) {
        if (col.hasTag(Config.MARIO_TAG)) {
            if (col.rect().pos.y + col.rect().h - dy * Time.deltaTime < rect.pos.y) {
                // Mario is squishing this goomba
                stateMachine.onEvent(Events.squish);
            }
            return;
        } else if (col.hasTag(Config.BRICK_TAG)) {
            Brick brick = (Brick) col;
            if (!(brick.getState() instanceof Brick.States.IdleState)) {
                stateMachine.onEvent(Events.knocked);
                return;
            }
        } else if (col.hasTag(Config.QUESTION_TAG)) {
            Question question = (Question) col;
            if (question.getState() instanceof Question.States.BounceState) {
                stateMachine.onEvent(Events.knocked);
                return;
            }
        } else if (col.hasTag(Config.TURTLE_TAG)) {
            Turtle turtle = (Turtle) col;
            if (turtle.getState() instanceof Turtle.States.MoveShell) {
                stateMachine.onEvent(Events.knocked);
                return;
            }
        } else if (col.hasTag(Config.SUPER_MUSHROOM_TAG)) {
            // Cancel collision
            return;
        }

        if (dy > 0) {
            vel.y = 0;
        } else if (dx != 0) {
            vel.x = -vel.x;
        }
    }

    private class Animation {
        private float animTimer = 0;
        private int animFrame = 0;

        public void runAnim() {
            sprite = SpriteAtlas.goomba[animFrame % 2];
            animTimer += Time.deltaTime;
            if (animTimer > 14 * Time.deltaTime) {
                animFrame += 1;
                animTimer = 0;
            }
        }
    }

    private static interface Events {
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
                hasCollider = false;
                
                ScoreSystem.addScore(Config.GOOMBA_SCORE);
            }

            public void update() {
                if(rect.pos.y > Config.FRAME_SIZE[1]) {
                    onEvent(Events.dead);
                }
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
                hasCollider = false;
                freezeMovement = true;

                ScoreSystem.addScore(Config.GOOMBA_SCORE);
            }
            
            public void update() {
                squishTimer += Time.deltaTime;
            }
        }

        public class DeadState extends State {
            public void onEnter(String event) {
                setActive(false);
            }
        }

    }
}
