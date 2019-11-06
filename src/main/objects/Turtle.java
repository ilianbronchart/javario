package src.main.objects;

import java.awt.*;

import src.main.basetypes.*;
import src.main.basetypes.Rectangle;
import src.main.globals.SpriteAtlas;
import src.main.globals.Time;
import src.main.Config;

public class Turtle extends GameObject {
    private StateMachine stateMachine;
    private States states = new States();
    private Animation animation;

    public Turtle(Rectangle rect) {
        super(Config.TURTLE_TAG, SpriteAtlas.Turtle.run[0], rect);
        gravity = true;
        isEntity = true;
        animation = new Animation();
        stateMachine = new StateMachine(states.new RunState());
        vel.x = -Config.ENTITY_START_VEL_X;
    }

    public void update() {
        stateMachine.update();
    }

    public State getState() {
        return stateMachine.getState();
    }

    @Override
    protected void setSprite(Image newSprite) {
        if (newSprite != sprite) {
            setCollider(rect.w, newSprite.getHeight(null));
            sprite = newSprite;
        }
    }

    private void shootShell(Mario mario) {
        if (mario.rect().pos.x + mario.rect().w < rect.pos.x + rect.w / 2) {
            vel.x = 0.5f;
        } else if (mario.rect().pos.x + mario.rect().w > rect.pos.x + rect.w / 2) {
            vel.x = -0.5f;
        } else if (mario.vel().x < 0) {
            vel.x = -0.5f;
        } else if (mario.vel().y > 0) {
            vel.x = 0.5f;
        } else {
            vel.x = -0.5f;
        }

        stateMachine.onEvent(Events.moveShell);

        // Move the turtle outside of mario's hitbox, so mario doesn't get killed
        if (vel.x > 0) {
            rect.pos.x = mario.rect().pos.x + mario.rect().w;
        } else if (vel.x < 0) {
            rect.pos.x = mario.rect().pos.x - rect.w;
        }
    }

    public void onCollision(GameObject col, float dx, float dy) {
        if (col.hasTag(Config.MARIO_TAG)) {

            if (stateMachine.getState() instanceof States.ShellState) {
                shootShell((Mario) col);
            }

            if (col.rect().pos.y + col.rect().h - dy * Time.deltaTime < rect.pos.y) {
                stateMachine.onEvent(Events.squish);
            }

            return;
        } else if (col.hasTag(Config.GOOMBA_TAG)) {
            if (stateMachine.getState() instanceof States.MoveShell) {
                // Cancel collision
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
        private double animTimer = 0;
        private int animFrame = 0;

        public void runAnim() {
            if (animTimer > 13 * Time.deltaTime) {
                animFrame++;
                animTimer = 0;
                setSprite(SpriteAtlas.Turtle.run[animFrame % 2]);
            }
            animTimer += Time.deltaTime;
        }
    }

    private interface Events {
        String squish = "squish";
        String moveShell = "move_shell";
    }

    public class States implements Events {
        public class RunState extends State {

            public State onEvent(String event) {
                if (event.equals(Events.squish)) {
                    return new ShellState();
                }

                return this;
            }

            public void update() {
                animation.runAnim();
            }
        }

        public class ShellState extends State {

            public State onEvent(String event) {
                if(event.equals(Events.moveShell)) {
                    return new MoveShell();
                }

                return this;
            }

            public void onEnter(String event) {
                setSprite(SpriteAtlas.Turtle.shell);
                vel.x = 0;

                ScoreSystem.addScore(Config.TURTLE_SCORE);
            }
        } 

        public class MoveShell extends State {}
    }
}
