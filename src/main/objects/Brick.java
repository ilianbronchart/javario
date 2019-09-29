package src.main.objects;

import src.main.basetypes.Rectangle;
import src.main.basetypes.State;
import src.main.basetypes.StateMachine;
import src.main.Config;
import src.main.basetypes.GameObject;
import src.main.globals.SpriteAtlas;

public class Brick extends GameObject {
    States states = new States();
    Animation animation;
    StateMachine stateMachine;
    boolean destroyed = false;

    public Brick(Rectangle rect) {
        super("brick", SpriteAtlas.brick, rect);

        animation = new Animation((int) rect.pos.y);
        stateMachine = new StateMachine(states.new IdleState());
    }

    public void update() {
        stateMachine.update();
        if (animation.animFrame == 0) {
            stateMachine.onEvent(Events.idle);
        }
    }

    public void onCollision(GameObject col, float dx, float dy) {
        if (dy >= 0) {
            // Brick was not hit from underside
            return;
        }

        if (col.tag.equals(Config.MARIO_TAG)) {
            Mario mario = (Mario) col;
            if(mario.marioSize == 0) {
                stateMachine.onEvent(Events.bounce);
            } else {
                stateMachine.onEvent(Events.smash);
            }
        }
    }

    public class Animation {
        int animFrame = 0;
        int startHeight;

        public Animation(int startHeight) {
            this.startHeight = startHeight;
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
        }

        public class BounceState extends State {
            public State onEvent(String event) {
                if (event.equals(idle)) {
                    return new IdleState();
                }

                return this;
            }

            public void update() {
                animation.bounceAnim();
            }
        }

        public class SmashState extends State {

        }
    }
}        

//     def draw(self, pos):
//         c.screen.blit(sprites.tile_set, (pos.x, pos.y), sprites.BRICK)

//     def instantiate_fragments(self):
//         """Instantiate fragments when broken"""
//         level.brick_fragments.append(Brick_Fragment(Vector2(self.pos.x, self.pos.y), Vector2(-0.1, -0.5), Rectangle()))
//         level.brick_fragments.append(Brick_Fragment(Vector2(self.pos.x + 24, self.pos.y), Vector2(0.1, -0.5), Rectangle()))
//         level.brick_fragments.append(Brick_Fragment(Vector2(self.pos.x + 24, self.pos.y + 24), Vector2(0.1, -0.4), Rectangle()))
//         level.brick_fragments.append(Brick_Fragment(Vector2(self.pos.x, self.pos.y + 24), Vector2(-0.1, -0.4), Rectangle()))

//     class Break_State(State):
//         """State when big mario hits brick from under"""
//         def __init__(self):
//             self.wait_for_frame = 0

//         def on_enter(self, owner_object):
//             owner_object.instantiate_fragments()
//             sounds.brick_smash.play()

//         def update(self, owner_object):
//             if self.wait_for_frame > 0:
//                 level.dynamic_colliders.remove(owner_object)
//             self.wait_for_frame += 1