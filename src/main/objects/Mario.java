package src.main.objects;

import java.awt.*;

import src.main.Config;
import src.main.basetypes.*;
import src.main.basetypes.Rectangle;
import src.main.globals.Keys;
import src.main.globals.SpriteAtlas;
import src.main.globals.Time;
import src.main.objects.Mario.States.BigMario;

public class Mario extends GameObject {
    States states = new States();
    StateMachine actionStates;
    StateMachine marioStates;
    Animation animation;

    boolean isJumping = false;

    public Mario(String tag, Rectangle rect){
        super(tag, Sprites.smallMarioIdle, rect);
        setSpriteOffset("CenterHorizontal");
        gravity = true;
        isEntity = true;
        animation = new Animation();
        marioStates = new StateMachine(states.new SmallMario());
        actionStates = new StateMachine(states.new IdleState());
        maxVel = Config.MARIO_MAX_VELOCITY;
    }
    
    @Override
    public void update() {
        actionStates.update();
        stateEvents();
        marioStates.update();
        animation.update();


        if (vel.x > 0) {
            flipSprite = false;
        } else if (vel.x < 0) {
            flipSprite = true;
        }
    }

    public void stateEvents() {
        if (freezeMovement) {  return; }

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

                if(Keys.down && marioStates.state instanceof BigMario){
                    actionStates.onEvent(Events.crouch);
                }
            }
        } else if (vel.y > 0) {
            // When mario falls off a ledge
            actionStates.onEvent(Events.falling);
        } else if (vel.y < 0) {
            // When mario is "jumping" but not int JumpState
            actionStates.onEvent(Events.resumeJump);
        }
    }

    public void onCollision(GameObject col, float dx, float dy) {
        if (col.tag.equals(Config.GOOMBA_TAG)) {
            if (rect.pos.y + rect.h - dy * Time.deltaTime < col.rect.pos.y) {
                // Mario is squishing the goomba
                actionStates.onEvent(Events.jump);
            } else {
                // Mario is running into the goomba
                if (marioStates.state instanceof States.BigMario) {
                    actionStates.onEvent(Events.shrink);
                }
                marioStates.onEvent(Events.shrink);
            }
        } else if (col.tag.equals(Config.SUPER_MUSHROOM_TAG) && marioStates.state instanceof States.SmallMario) {
            actionStates.onEvent(Events.grow);
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

    @Override
    public void setSprite(Image newSprite) {
        setSpriteOffset("CenterHorizontal");
        setCollider(rect.w, newSprite.getHeight(null));
        sprite = newSprite;
    }

    static class Sprites extends SpriteSet {
        static Image marioDead = SpriteAtlas.tileSet.getSubimage(240, 168, 48, 48);

        // IDLE
        static Image smallMarioIdle = SpriteAtlas.tileSet.getSubimage(288, 168, 48, 48);
        static Image mediumMarioIdle = SpriteAtlas.tileSet.getSubimage(48, 327, 48, 72);
        static Image bigMarioIdle = SpriteAtlas.tileSet.getSubimage(288, 216, 48, 96);

        // JUMP
        static Image smallMarioJump = SpriteAtlas.tileSet.getSubimage(192, 168, 48, 48);
        static Image bigMarioJump = SpriteAtlas.tileSet.getSubimage(192, 216, 48, 96);

        // BRAKE
        static Image smallMarioBrake = SpriteAtlas.tileSet.getSubimage(144, 168, 48, 48);
        static Image bigMarioBrake = SpriteAtlas.tileSet.getSubimage(144, 216, 48, 96);

        // CROUCH
        static Image marioCrouch = SpriteAtlas.tileSet.getSubimage(240, 246, 48, 66);

        static Image[] smallMarioRun = new Image[] {
            SpriteAtlas.tileSet.getSubimage(0, 168, 48, 48),
            SpriteAtlas.tileSet.getSubimage(48, 168, 48, 48),
            SpriteAtlas.tileSet.getSubimage(96, 168, 48, 48)
        };

        static Image[] bigMarioRun = new Image[] {
            SpriteAtlas.tileSet.getSubimage(96, 216, 48, 96),
            SpriteAtlas.tileSet.getSubimage(48, 216, 48, 96),
            SpriteAtlas.tileSet.getSubimage(0, 216, 48, 96)
        };
    }

    interface Animations {
        String runAnim = "runAnim";
        String deathAnim = "deathAnim";
        String growAnim = "growAnim";
        String shrinkAnim = "shrinkAnim";
    }

    class Animation implements Animations {
        private String currentAnimation = "";

        int animFrame = 0;
        double animTimer = 0;

        int[] runFrames = {0, 1, 2, 1};
        int[] growFrames = {1, 0, 1, 0, 1, 2};
        int[] shrinkFrames = {1, 2, 1, 2, 1, 0};


        public void clearAnimation() {
            this.currentAnimation = "";
        }

        public void setAnimation(String animation) {
            currentAnimation = animation;
            animFrame = 0;
            animTimer = 0;
        }

        public boolean animationTerminated() {
            return this.currentAnimation.equals("");
        }

        public void update() {
            animTimer += Time.deltaTime;
            System.out.println(currentAnimation);
            switch (currentAnimation) {
                case runAnim:
                    runAnim();
                    break;
                case deathAnim:
                    deathAnim();
                    break;
                case growAnim:
                    growAnim();
                    break;
                case shrinkAnim:
                    shrinkAnim();
                    break;
            }
        }

        public void runAnim() {
            if (marioStates.state instanceof States.SmallMario) {
                setSprite(Sprites.smallMarioRun[runFrames[animFrame % 4]]);
            } else {
                setSprite(Sprites.bigMarioRun[runFrames[animFrame % 4]]);
            }

            if (animTimer > (6 * Time.deltaTime) * animFrame) {
                animFrame++;
            }
        }

        public void deathAnim() {
            if (animTimer > 14 * Time.deltaTime) {
                freezeMovement = false;
            }
        }

        public void growAnim() {
            if (animTimer > (10 * Time.deltaTime) * animFrame) {
                switch (growFrames[animFrame]) {
                    case 0:
                        setSprite(Sprites.smallMarioIdle);
                        break;
                    case 1:
                        setSprite(Sprites.mediumMarioIdle);
                        break;
                    case 2:
                        setSprite(Sprites.bigMarioIdle);
                        break;
                }

                if (growFrames[animFrame] == 2) {
                    clearAnimation();
                }

                animFrame++;
            }
        }

        public void shrinkAnim() {
            if (animTimer > (10 * Time.deltaTime) * animFrame) {
                switch (shrinkFrames[animFrame]) {
                    case 0:
                        setSprite(Sprites.smallMarioIdle);
                        break;
                    case 1:
                        setSprite(Sprites.mediumMarioIdle);
                        break;
                    case 2:
                        setSprite(Sprites.bigMarioIdle);
                        break;
                }

                if (shrinkFrames[animFrame] == 0) {
                    clearAnimation();
                }

                animFrame++;
            }
        }

            // def shrink_anim(self):
    //     """Animation when shrinking"""
    //     self.current_sprite = sprites.SHRINK_SPRITES[self.shrink_frames[self.anim_frame]]
    //     if self.anim_timer > 6 * c.delta_time:
    //         self.anim_frame += 1
    //         self.anim_timer = 0
    //     self.new_y = self.start_height + (self.start_sprite_height - self.current_sprite[3])
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
    //     self.shrink_frames = [0, 1, 0, 1, 2, 1, 2, 1]
    //     self.start_sprite_height = 0

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
        String resumeJump = "resumeJump";
        String move = "move";
        String decel = "decel";
        String brake = "brake";
        String crouch = "crouch";

        // Mario events
        String shrink = "shrink";
        String grow = "grow";
        String win = "win";
        String dead = "dead";
        String bigMario = "bigMario";
        String smallMario = "smallMario";
    }

    class States implements Events {
        class IdleState extends State {
            public void onEnter(String event) {
                refreshSprite();

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
                    case grow: // Fallthrough
                    case shrink:
                        return new PowerupState();
                }
                
                return this;
            }

            public void refreshSprite() {
                if(marioStates.state instanceof States.SmallMario) {
                    setSprite(Sprites.smallMarioIdle);
                } else {
                    setSprite(Sprites.bigMarioIdle);
                }
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
                    case grow: // Fallthrough
                    case shrink:
                        return new PowerupState();
                }

                return this;
            }

            public void onEnter(String event) {
                refreshSprite();

                isJumping = true;
                friction = 1;
                
                if (event != Events.falling && event != Events.resumeJump) {
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

            public void refreshSprite() {
                if (marioStates.state instanceof States.SmallMario) {
                    setSprite(Sprites.smallMarioJump);
                } else {
                    setSprite(Sprites.bigMarioJump);
                }
            }
        }
        
        public class MoveState extends State {
            //State when moving on the ground and not breaking or decelerating

            public void onEnter(String event) {
                animation.setAnimation(Animations.runAnim);
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
                    case grow: // Fallthrough
                    case shrink:
                        return new PowerupState();
                }
                    
                    return this;
                }

            public void onExit() {
                animation.clearAnimation();;
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
                    case grow: // Fallthrough
                    case shrink:
                        return new PowerupState();
                }

                return this;
            }

            public void onEnter(String event) {
                refreshSprite();

                friction = Config.BRAKE_FRICTION;
                acceleration = 0;
            }

            public void refreshSprite() {
                if (marioStates.state instanceof States.SmallMario) {
                    setSprite(Sprites.smallMarioBrake);
                } else {
                    setSprite(Sprites.bigMarioBrake);
                }
            }
        }

        public class DecelState extends State {
            // State when moving when there is no longer any input

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
                    case grow: // Fallthrough
                    case shrink:
                        return new PowerupState();
                }

                return this;
            }

            public void onEnter(String event) {
                animation.setAnimation(Animations.runAnim);
                acceleration = 0;
                friction = Config.DECEL_FRICTION; 
            }


            public void onExit() {
                animation.clearAnimation();
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
                    case grow: // Fallthrough
                    case shrink:
                        return new PowerupState();
                }

                return this;
            }

            public void onEnter(String event) {
                setSprite(Sprites.marioCrouch);
                acceleration = 0;
                friction = Config.BRAKE_FRICTION;
            }

            public void update() {
                if (vel.x == 0) {
                    if (Keys.left) {
                        flipSprite = true;
                    }
                    if (Keys.right) {
                        flipSprite = false;
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

        public class PowerupState extends State {
            String powerupType;

            public State onEvent(String event) {
                switch(event) {
                    case move:
                        return new MoveState();
                    case decel:
                        return new DecelState();
                    case resumeJump:
                        return new JumpState();
                    case brake:
                        return new BrakeState();
                    case idle:
                        return new IdleState();
                    case crouch:
                        return new CrouchState();
                }

                return this;
            }

            public void onEnter(String event) {
                freezeMovement = true;

                switch (event) {
                    case grow:
                        animation.setAnimation(Animations.growAnim);
                        break;
                    case shrink:
                        animation.setAnimation(Animations.shrinkAnim);
                        break;
                    }

                powerupType = event;
            }

            public void update() {
                if (animation.animationTerminated()) {
                    freezeMovement = false;

                    switch (powerupType) {
                        case grow:
                            marioStates.onEvent(bigMario);
                            break;
                        case shrink:
                            marioStates.onEvent(smallMario);
                            break;
                    }
                }
            }
        }

// MARIO STATES _________________________________________________________________________

        public class SmallMario extends State {
            public State onEvent(String event) {
                switch (event) {
                    case shrink:
                        return new DeadMario();
                    case win:
                        // return new WinState();
                    case dead:
                        return new DeadMario();
                    case bigMario:
                        return new BigMario();
                }

                return this;
            }
        }

        public class BigMario extends State {
            public State onEvent(String event) {
                switch (event) {
                    case dead:
                        return new DeadMario();
                    case smallMario:
                        return new SmallMario();
                    case win:
                        // return new WinState();
                }

                return this;
            }
        }

        public class DeadMario extends State {
            public void onEnter(String state) {
                actionStates.disable();
                animation.setAnimation(Animations.deathAnim);
                setSprite(Sprites.marioDead);
                vel = new Vector2(0, Config.JUMP_VELOCITY);
                acceleration = 0;
                hasCollider = false;
                freezeMovement = true;
            }

            public void update() {
                if (rect.pos.y > Config.FRAME_SIZE[1]) {
                    isAwake = false;
                    triggerScene(Config.Scenes.MAIN_MENU);
                }
            }
        }
    }
}

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
 