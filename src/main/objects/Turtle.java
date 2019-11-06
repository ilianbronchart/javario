package src.main.objects;

import java.awt.*;

import src.main.basetypes.*;
import src.main.basetypes.Rectangle;
import src.main.globals.SpriteAtlas;
import src.main.globals.Time;
import src.main.Config;

public class Turtle extends GameObject {
    public StateMachine stateMachine;
    public States states = new States();
    Animation animation;

    boolean canKill = false;

    public Turtle(Rectangle rect) {
        super(Config.TURTLE_TAG, SpriteAtlas.turtleRun[0], rect);
        gravity = true;
        isEntity = true;
        animation = new Animation();
        stateMachine = new StateMachine(states.new RunState());
        vel.x = -Config.ENTITY_START_VEL_X;
    }

    public void update() {
        stateMachine.update();
    }

    @Override
    public void setSprite(Image newSprite) {
        if (newSprite != sprite) {
            setCollider(rect.w, newSprite.getHeight(null));
            sprite = newSprite;
        }
    }

    private void shootShell(Mario mario) {
        if (mario.rect.pos.x + mario.rect.w < rect.pos.x + rect.w / 2) {
            vel.x = 0.5f;
        } else if (mario.rect.pos.x + mario.rect.w > rect.pos.x + rect.w / 2) {
            vel.x = -0.5f;
        } else if (mario.vel.x < 0) {
            vel.x = -0.5f;
        } else if (mario.vel.y > 0) {
            vel.x = 0.5f;
        } else {
            vel.x = -0.5f;
        }

        stateMachine.onEvent(Events.moveShell);

        // Move the turtle outside of mario's hitbox, so mario doesn't get killed
        if (vel.x > 0) {
            rect.pos.x = mario.rect.pos.x + mario.rect.w;
        } else if (vel.x < 0) {
            rect.pos.x = mario.rect.pos.x - rect.w;
        }
    }

    public void onCollision(GameObject col, float dx, float dy) {
        if (col.tag.equals(Config.MARIO_TAG)) {

            if (stateMachine.state instanceof States.ShellState) {
                shootShell((Mario) col);
                return;
            }

            if (col.rect.pos.y + col.rect.h - dy * Time.deltaTime < rect.pos.y) {
                stateMachine.onEvent(Events.squish);
                return;
            }
        } else if (col.tag.equals(Config.GOOMBA_TAG)) {
            if (stateMachine.state instanceof States.MoveShell) {
                // Cancel collision
                return;
            }
        }

        if (dy > 0) {
            vel.y = 0;
        } else if (dx != 0) {
            vel.x = -vel.x;
        }
    }

    public class Animation {
        private double animTimer = 0;
        private int animFrame = 0;

        public void runAnim() {
            if (animTimer > 13 * Time.deltaTime) {
                animFrame++;
                animTimer = 0;
                setSprite(SpriteAtlas.turtleRun[animFrame % 2]);
            }
            animTimer += Time.deltaTime;
        }
    }

    interface Events {
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
                setSprite(SpriteAtlas.turtleShell);
                vel.x = 0;
            }
        } 

        public class MoveShell extends State {

        }
    }
}


//             if dx > 0:
//                 self.pos.x = other_collider.pos.x - self.rect.w
//                 self.vel.x = -self.vel.x
//             elif dx < 0:
//                 self.pos.x = other_collider.pos.x + other_collider.rect.w
//                 self.vel.x = -self.vel.x
//             elif dy > 0:
//                 self.pos.y = other_collider.pos.y - self.rect.h
//                 self.vel.y = 0

//         if other_enemy is not None:
//             if self.state_machine.get_state() != 'Move_Shell':
//                 self.pos.x -= dx * c.delta_time
//                 self.vel.x = -self.vel.x
//             else:
//                 other_enemy.state_machine.on_event('knocked')
//                 other_enemy.is_active = True


