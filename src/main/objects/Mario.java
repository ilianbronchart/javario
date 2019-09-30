package src.main.objects;

import java.awt.*;

import src.main.Config;
import src.main.basetypes.*;
import src.main.basetypes.Rectangle;
import src.main.globals.Keys;
import src.main.globals.SpriteAtlas;
import src.main.globals.Time;

public class Mario extends GameObject {
    States states = new States();
    StateMachine actionStates;
    StateMachine marioStates;
    Animation animation;

    int marioSize = 0;

    boolean isJumping = false;

    public Mario(String tag, Rectangle rect){
        super(tag, Sprites.marioIdle[0], rect);
        gravity = true;
        isEntity = true;
        animation = new Animation();
        marioStates = new StateMachine(states.new SmallMario());
        actionStates = new StateMachine(states.new IdleState());
        maxVel = Config.MARIO_MAX_VELOCITY;
    }
    
    @Override
    public void update() {
        // if self.current_mario_state != 'Invincible_Mario':
        //     self.mario_states.update()
        
        actionStates.update();
        stateEvents();
        marioStates.update(); // Mario states take precedence over action states.

        if (vel.x > 0) {
            flipSprite = false;
        } else if (vel.x < 0) {
            flipSprite = true;
        }

        //     if self.current_mario_state == 'Invincible_Mario':
        //         self.mario_states.update()
        
        //     self.rect.h = self.animation.current_sprite[3]
    
        // if (pos.y > Config.FRAME_SIZE[1]) {
        //     marioStates.onEvent(Events.dead);
        // }
    }

    public void stateEvents() {
        if (vel.y == 0) {
            if (Keys.space && !Keys.spacePressed && !isJumping) {
                actionStates.onEvent(Events.jump);
            } else {
                
                if(Keys.left || Keys.right) {
                    if (vel.x < 0 && Keys.right || vel.x > 0 && Keys.left) {
                        actionStates.onEvent(Events.brake);
                    } else {
                        actionStates.onEvent(Events.move);
                    }
                    
                } else if (vel.x != 0) {
                    actionStates.onEvent(Events.decel);
                }
                
                if (Math.abs(vel.x) < Config.MIN_STOP_VELOCITY && acceleration == 0) {
                    actionStates.onEvent(Events.idle);
                }
            }
        } else {
            if (vel.y > 0) {
                actionStates.onEvent(Events.falling);
            }
        }

        // if self.current_mario_state == 'Big_Mario':
        //     if self.crouch:
        //         self.action_states.on_event('crouch')
    }

    public void onCollision(GameObject col, float dx, float dy) {
        if (col.tag.equals(Config.GOOMBA_TAG)) {
            if (rect.pos.y + rect.h - dy * Time.deltaTime < col.rect.pos.y) {
                // Mario is squishing the goomba
                actionStates.onEvent(Events.jump);
            } else {
                // Mario is running into the goomba
                marioStates.onEvent(Events.shrink);
            }
        }

        if (!col.isEntity) {
            if (dx != 0) {
                vel.x = 0;
                acceleration = 0;
            } else if (dy > 0) {
                vel.y = 0;
            } else if (dy < 0) {
                vel.y = Config.BUMP_VEL;
            }
        }
    }

    static class Sprites extends SpriteSet {
        static Image marioDead = SpriteAtlas.tileSet.getSubimage(240, 168, 48, 48);

        static Image[] smallMarioRun = new Image[] {
            SpriteAtlas.tileSet.getSubimage(0, 168, 48, 48),
            SpriteAtlas.tileSet.getSubimage(48, 168, 48, 48),
            SpriteAtlas.tileSet.getSubimage(96, 168, 48, 48)
        };

        static Image[] marioIdle = {
            SpriteAtlas.tileSet.getSubimage(294, 168, 36, 48), // Small
            SpriteAtlas.tileSet.getSubimage(288, 216, 48, 96)  // Big
        };

        static Image[] marioBrake = {
            SpriteAtlas.tileSet.getSubimage(144, 168, 48, 48), // Small
            SpriteAtlas.tileSet.getSubimage(144, 216, 48, 96)  // Big
        };
        
        static Image[] marioJump = {
            SpriteAtlas.tileSet.getSubimage(192, 168, 48, 48), // Small
            SpriteAtlas.tileSet.getSubimage(144, 216, 48, 96)  // Big    
        };
    }

    class Animation {
        int currentFrame = 0;
        double animTimer = 0;

        int[] runFrames = {0, 1, 2, 1};

        public void reset() {
            currentFrame = 0;
            animTimer = 0;
        }

        public void runAnim() {
            animTimer += Time.deltaTime;

            if (marioSize == 0) {
                sprite = Sprites.smallMarioRun[runFrames[currentFrame % 4]];
            } else {
                //sprite = Sprites.bigMarioRun[runFrames[currentFrame % 4]];
            }

            if (animTimer > 6 * Time.deltaTime) {
                currentFrame++;
                animTimer = 0;
            }
        }

        public void deathAnim() {
            animTimer += Time.deltaTime;
            if (animTimer > 14 * Time.deltaTime) {
                freezeMovement = false;
            }
        }
    }

    // class Animation():
    // """Contains specific animation variables and functions for this class"""
    // def __init__(self):
    //     self.current_sprite = sprites.SMALL_MARIO_IDLE

    //     self.mario_size = 'Small_Mario'
    //     self.anim_frame = 0
    //     self.anim_timer = c.INITIAL_TIMER_VALUE
    //     self.invincible_timer = 0

    //     self.start_height = None
    //     self.new_y = self.start_height

    //     self.grow_frames = [0, 1, 0, 1, 2, 0, 1, 2]
    //     self.shrink_frames = [0, 1, 0, 1, 2, 1, 2, 1]
    //     self.start_sprite_height = 0

    // def reset_anim_vars(self):
    //     """Reset animation variables"""
    //     self.anim_frame = 0
    //     self.anim_timer = c.INITIAL_TIMER_VALUE

    // def grow_anim(self):
    //     """Animation when growing"""
    //     self.current_sprite = sprites.GROW_SPRITES[self.grow_frames[self.anim_frame]]
    //     if self.anim_timer > 6 * c.delta_time:
    //         self.anim_frame += 1
    //         self.anim_timer = 0
    //     self.new_y = self.start_height - (self.current_sprite[3] - 48)

    // def shrink_anim(self):
    //     """Animation when shrinking"""
    //     self.current_sprite = sprites.SHRINK_SPRITES[self.shrink_frames[self.anim_frame]]
    //     if self.anim_timer > 6 * c.delta_time:
    //         self.anim_frame += 1
    //         self.anim_timer = 0
    //     self.new_y = self.start_height + (self.start_sprite_height - self.current_sprite[3])

    // def win_anim_on_flag(self):
    //     """Animation when sliding down flag pole"""
    //     if self.mario_size == 'Small_Mario':
    //         self.current_sprite = sprites.WIN_SPRITES_SMALL[self.anim_frame % 2]
    //     else:
    //         self.current_sprite = sprites.WIN_SPRITES_BIG[self.anim_frame % 2]
    //     if self.anim_timer > 8 * c.delta_time:
    //         self.anim_frame += 1
    //         self.anim_timer = 0

    interface Events {
        // Action events
        String idle = "idle";
        String jump = "jump";
        String falling = "falling";
        String move = "move";
        String decel = "decel";
        String brake = "brake";
        String crouch = "crouch";

        // Mario events
        String shrink = "shrink";
        String grow = "grow";
        String win = "win";
        String dead = "dead";
    }

    class States implements Events {
        class IdleState extends State {
            public void onEnter(String event) {
                sprite = Sprites.marioIdle[marioSize];
                vel.x = 0;
            }

            public State onEvent(String event) {
                switch (event) {
                    case jump:
                        return new JumpState();
                    case move:
                        return new MoveState();
                    case decel:
                        return new DecelState();
                    case brake:
                        return new BrakeState();
                    case crouch:
                        return new CrouchState();
                    }
                
                return this;
            }
        }

        public class JumpState extends State {
            // State when jumping when spacebar input affects velocity

            public State onEvent(String event) {
                switch (event){
                    case idle:
                        return new IdleState();
                    case decel:
                        return new DecelState();
                    case brake:
                        return new BrakeState();
                    case move:
                        return new MoveState();  
                    case jump:
                        return new JumpState();
                }

                return this;
            }

            public void onEnter(String event) {
                sprite = Sprites.marioJump[marioSize];
                isJumping = true;
                friction = 1;
                
                if (event != Events.falling) {
                    vel.y = Config.JUMP_VELOCITY;
                }

                // if (marioStates.state instanceof SmallMario ) {
                //     sounds.smallJump.play();
                // } else {
                //     sounds.bigJump.play();
                // }
            }

            public void update() {
                if(Keys.left){
                    acceleration = -Config.MARIO_ACCELERATION;
                } else if (Keys.right) {
                    acceleration = Config.MARIO_ACCELERATION;
                } else {
                    friction = Config.DECEL_FRICTION;
                    acceleration = 0;
                }
            }

            public void onExit() {
                isJumping = false;
            }
        }
        
        public class MoveState extends State {
            //State when moving on the ground and not breaking or decelerating

            public void onEnter(String event) {
                friction = 1;
                acceleration = Config.MARIO_ACCELERATION;
                acceleration *= Keys.left ? -1 : 1;
            }
            
            public State onEvent(String event) {
                switch (event) {
                    case decel:
                        return new DecelState();
                    case brake:
                        return new BrakeState();
                    case falling: // Fallthrough
                    case jump:
                        return new JumpState();
                    case crouch:
                        return new CrouchState();
                    case idle:
                        return new IdleState();
                    }
                    
                    return this;
                }
                
            public void update() {
                animation.runAnim();
            }
        }

        public class BrakeState extends State {
            // State when input is opposite velocity
            
            public State onEvent(String event) {
                switch (event) {
                    case move:
                        return new MoveState();
                    case decel:
                        return new DecelState();
                    case falling: // Fallthrough
                    case jump:
                        return new JumpState();
                    case crouch:
                        return new CrouchState();
                    case idle:
                        return new IdleState();
                }

                return this;
            }

            public void onEnter(String event) {
                sprite = Sprites.marioBrake[marioSize];
                friction = Config.BRAKE_FRICTION;
                acceleration = 0;
            }
        }

        public class DecelState extends State {
            // State when moving when there is no longer any input

            public void onEnter(String event) {
                acceleration = 0;
                friction = Config.DECEL_FRICTION; 
            }

            public State onEvent(String event) {
                switch (event) {
                    case idle:
                        return new IdleState();
                    case brake:
                        return new BrakeState();
                    case move:
                        return new MoveState();
                    case falling: // Fallthrough
                    case jump:
                        return new JumpState();
                    case crouch:
                        return new CrouchState();
                }

                return this;
            }

            public void update() {
                animation.runAnim();
            }
        }

        public class CrouchState extends State {
            // State when mario is crouching

            public State onEvent(String event) {
                switch (event) {
                    case brake:
                        return new BrakeState();
                    case jump:
                        return new JumpState();
                    case decel:
                        return new DecelState();
                    case move:
                        return new MoveState();
                    case idle:
                        return new IdleState();
                }

                return this;
            }

            public void onEnter(String event) {
                // owner_object.animation.current_sprite = sprites.MARIO_CROUCH
                acceleration = 0;
                friction = Config.BRAKE_FRICTION;
                rect.pos.y += 30;
                // owner_object.rect.h = owner_object.animation.current_sprite[3]
            }

            public void update() {
                if (vel.x == 0) {
                    if (Keys.left) {
                        flipSprite = true;
                    }
                    if (Keys.right) {
                        flipSprite = true;
                    }
                }
            }
        }

        public class InvincibleMario extends State {
            // State after shrinking when mario is invincible

            int invincibleTimer;
            int blinkTimer;

            public InvincibleMario() {
                invincibleTimer = 0;
                blinkTimer = 0;
            }

            public State onEvent(String event) {
                if(event.equals(Events.shrink)){
                    // return new SmallMario();
                }
                return this;
            }

            public void update() {
                invincibleTimer += Time.deltaTime;

                if (invincibleTimer > 40 * Time.deltaTime) {
                    onEvent(Events.shrink);
                }

                blinkTimer += Time.deltaTime;
                if (blinkTimer > 7 * Time.deltaTime) {
                    // animation.currentSprite = sprites.EMPTY_SPRITE;
                    if(blinkTimer > 14 * Time.deltaTime) {
                        blinkTimer = 0;
                    }
                }
            }

            public void onExit() {
                // animation.resetAnimVars();
            }
        }

        public class SmallMario extends State {
            public State onEvent(String event) {
                switch (event) {
                    case grow:
                        // return new GrowMario();
                    case shrink:
                        return new DeadMario();
                    case win:
                        // return new WinState();
                    case dead:
                        return new DeadMario();
                }

                return this;
            }
        }

        public class DeadMario extends State {
            public void onEnter(String state) {
                sprite = Sprites.marioDead;
                vel.y = Config.JUMP_VELOCITY;
                vel.x = 0;
                acceleration = 0;
                hasCollider = false;
                freezeMovement = true;
                actionStates.disable();
                animation.reset();
            }

            public void update() {
                animation.deathAnim();
                if (rect.pos.y > Config.FRAME_SIZE[1]) {
                    isAwake = false;
                    triggerScene(Config.Scenes.MAIN_MENU);
                }
            }
        }

        // public class GrowMario extends State {
        //     public State onEvent(String event) {
        //         switch (event) {
        //             case
        //         }

        //         return this;
        //     }
        // }
    }
}
        
    // class Grow_Mario(State):
    //     """State when mario is growing"""
    //     def on_event(self, event):
    //         if event == 'big mario':
    //             return Mario.Big_Mario()
    //         if event == 'shrink':
    //             return Mario.Shrink_Mario()
    //         return self

    //     def on_enter(self, owner_object):
    //         owner_object.animation.start_height = owner_object.pos.y
    //         owner_object.animation.reset_anim_vars()
    //         owner_object.freeze_movement = True

    //     def update(self, owner_object):
    //         owner_object.animation.grow_anim()
    //         owner_object.pos.y = owner_object.animation.new_y
    //         if owner_object.animation.anim_frame > 7:
    //             owner_object.mario_states.on_event('big mario')

    //     def on_exit(self, owner_object):
    //         owner_object.rect.h = 96
    //         owner_object.animation.mario_size = 'Big_Mario'
    //         owner_object.animation.reset_anim_vars()
    //         owner_object.freeze_movement = False

    // class Big_Mario(State):
    //     """State when mario is big"""
    //     def on_event(self, event):
    //         if event == 'shrink':
    //             return Mario.Shrink_Mario()
    //         elif event == 'dead':
    //             return Mario.Dead_Mario()
    //         elif event == 'win':
    //             return Mario.Win_State()
    //         return self

    // class Shrink_Mario(State):
    //     """State when mario is shrinking"""
    //     def on_event(self, event):
    //         if event == 'invincible':
    //             return Mario.Invincible_Mario()
    //         if event == 'grow mario':
    //             return Mario.Grow_Mario()
    //         return self

    //     def on_enter(self, owner_object):
    //         owner_object.animation.reset_anim_vars()
    //         owner_object.animation.start_height = owner_object.pos.y
    //         owner_object.animation.start_sprite_height = owner_object.animation.current_sprite[3]
    //         owner_object.freeze_movement = True
    //         sounds.pipe.play()

    //     def update(self, owner_object):
    //         owner_object.animation.shrink_anim()
    //         owner_object.pos.y = owner_object.animation.new_y
    //         if owner_object.animation.anim_frame > 7:
    //             owner_object.mario_states.on_event('invincible')

    //     def on_exit(self, owner_object):
    //         owner_object.rect.h = 48
    //         owner_object.animation.mario_size = 'Small_Mario'
    //         owner_object.animation.reset_anim_vars()
    //         owner_object.freeze_movement = False

    //     def on_exit(self, owner_object):
    //         owner_object.pos.y -= 31
    //         owner_object.start_height = owner_object.pos.y

    // class Win_State(State):
    //     """State when mario wins, runs and manages events related to the final win animation"""
    //     def __init__(self):
    //         self.animation_step = 0
    //         self.timer = 0

    //     def on_event(self, event):
    //         return self

    //     def on_enter(self, owner_object):
    //         owner_object.animation.reset_anim_vars()
    //         owner_object.animation.start_height = owner_object.pos.y
    //         owner_object.animation.new_y = owner_object.pos.y
    //         owner_object.pos.x = c.flagpole.pos.x - 16
    //         owner_object.freeze_movement = True
    //         owner_object.freeze_input = True
    //         owner_object.vel = Vector2()
    //         pg.mixer.music.stop()
    //         sounds.flagpole_sound.play()

    //     def update(self, owner_object):

    //         if self.animation_step == 0:
    //             owner_object.animation.win_anim_on_flag()
    //             owner_object.pos.y += 4
    //             if owner_object.pos.y > c.flagpole.pos.y + c.flagpole.rect.h - 100:
    //                 self.animation_step = 1

    //         elif self.animation_step == 1:
    //             owner_object.pos.x = c.flagpole.pos.x + 24
    //             owner_object.flip_sprites = True
    //             self.timer += c.delta_time
    //             if self.timer > 20 * c.delta_time:
    //                 owner_object.flip_sprites = False
    //                 owner_object.freeze_movement = False
    //                 owner_object.pos.x = c.flagpole.pos.x + c.flagpole.rect.w
    //                 self.animation_step = 2
    //                 pg.mixer.music.set_endevent(c.WIN_SONG_END)
    //                 pg.mixer.music.load(sounds.stage_clear)
    //                 pg.mixer.music.play()

    //         elif self.animation_step == 2:
    //             c.ACCELERATION = c.MARIO_ACCELERATION
    //             owner_object.Pressed_right = True
    //             if owner_object.pos.x > c.LEVEL_END_X:
    //                 owner_object.freeze_movement = True
    //                 c.final_count_down = True
