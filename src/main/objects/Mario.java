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
    boolean winCondition = false;

    boolean isInvincible = false; // Brief period of invincibility after mario shrinks
    private double invincibilityTimer = 0;

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
        handleWinCondition();
        actionStates.update();
        stateEvents();
        marioStates.update();
        animation.update();
        handleInvincibility();

        if (vel.x > 0) {
            flipSprite = false;
        } else if (vel.x < 0) {
            flipSprite = true;
        }
    }

    public void handleInvincibility() {
        if (isInvincible) {
            if (invincibilityTimer > 80 * Time.deltaTime) {
                isInvincible = false;
                invincibilityTimer = 0;
            }
            invincibilityTimer += Time.deltaTime;
        }
    }

    public void handleWinCondition() {
        if (winCondition) {
            Keys.right = true;
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
        if (col.hasTag(Config.GOOMBA_TAG)) {
            if (rect.pos.y + rect.h - dy * Time.deltaTime < col.rect.pos.y) {
                // Mario is squishing the goomba
                actionStates.onEvent(Events.jump);
                rect.pos.y = col.rect.pos.y - rect.h;
            } else {
                // Mario is running into the goomba
                if (marioStates.state instanceof States.BigMario) {
                    actionStates.onEvent(Events.shrink);
                }

                if (!isInvincible) {
                    marioStates.onEvent(Events.shrink);
                }
            }

            return;
        }

        if (col.hasTag(Config.TURTLE_TAG)) {
            Turtle turtle = (Turtle) col;   

            if (rect.pos.y + rect.h - dy * Time.deltaTime < col.rect.pos.y) {
                // Mario is squishing the turtle
                if (!(turtle.stateMachine.state instanceof Turtle.States.ShellState)) {
                    actionStates.onEvent(Events.jump);
                }
            } else {
                // Mario is running into the turtle
                if (!(turtle.stateMachine.state instanceof Turtle.States.ShellState)) {
                    System.out.println(turtle.stateMachine.state);
                    if (marioStates.state instanceof States.BigMario) {
                        actionStates.onEvent(Events.shrink);
                    }

                    if (!isInvincible) {
                        marioStates.onEvent(Events.shrink);
                    }
                }
            }

            return;
        }

        if (col.hasTag(Config.SUPER_MUSHROOM_TAG)) {
            if (marioStates.state instanceof States.SmallMario) {
                actionStates.onEvent(Events.grow);
            }
        }

        if (col.hasTag(Config.FLAGPOLE_TAG)) {
            if (rect.pos.y < col.rect.pos.y + col.rect.h) {
                rect.pos.x = col.rect.pos.x + 30;
                actionStates.onEvent(Events.flagPole);
            } 

            if (rect.pos.y + rect.h > col.rect.pos.y + col.rect.h - 20) {
                actionStates.onEvent(Events.win);
            }
            return;
        }

        if (col.hasTag(Config.WIN_TRIGGER_TAG)) {
            triggerScene(Config.Scenes.MAIN_MENU);
            Keys.unFreezeInput();
        }

        if (dx != 0) {
            vel.x = 0;
            acceleration = 0;
        } else if (dy > 0) {
            vel.y = 0;
        } else if (dy < 0) {
            vel.y = Config.BUMP_VEL;
        }
    }

    @Override
    public void setSprite(Image newSprite) {
        if (newSprite != sprite) {
            setSpriteOffset("CenterHorizontal");
            setCollider(rect.w, newSprite.getHeight(null));
            sprite = newSprite;
        }
    }

    static class Sprites extends SpriteSet {
        static Image marioDead = SpriteAtlas.tileSet.getSubimage(240, 168, 48, 48);
        static Image invincibilitySprite = SpriteAtlas.tileSet.getSubimage(240, 48, 48, 48);

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

        static Image[] smallMarioFlag = {
            SpriteAtlas.tileSet.getSubimage(387, 168, 42, 48),
            SpriteAtlas.tileSet.getSubimage(339, 168, 42, 48)
        };
        
        static Image[] bigMarioFlag = {
            SpriteAtlas.tileSet.getSubimage(387, 222, 42, 90),
            SpriteAtlas.tileSet.getSubimage(339, 222, 42, 90)
        };

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

    class Animation {
        private Runnable currentAnimation = () -> {};
        private boolean animationTerminated = false;

        private int animFrame = 0;
        private double animTimer = 0;

        private double invisibilityTimer = 0;

        private int[] runFrames = {0, 1, 2, 1};
        private int[] growFrames = {1, 0, 1, 0, 1, 2};
        private int[] shrinkFrames = {1, 2, 1, 2, 1, 0};

        public void setAnimation(Runnable animation) {
            currentAnimation = animation;
            animationTerminated = false;
            animFrame = 0;
            animTimer = 0;
        }
        
        public void clearAnimation() {
            currentAnimation = () -> {};
            animationTerminated = true;
        }

        public boolean animationTerminated() {
            return animationTerminated;
        }

        public void update() {
            currentAnimation.run();

            if (isInvincible) {
                invincibleAnim();
            }

            animTimer += Time.deltaTime;
        }

        public void flagAnim() {
            if (animTimer > 10 * Time.deltaTime * animFrame) {
                animFrame++;
                if (marioStates.state instanceof States.SmallMario) {
                    setSprite(Sprites.smallMarioFlag[animFrame % 2]);
                } else {
                    setSprite(Sprites.bigMarioFlag[animFrame % 2]);
                }
            }
        }

        public void invincibleAnim() {
            // Alternate between invisible and visible every 7 frames
            if (invisibilityTimer > 7 * Time.deltaTime) {
                setSprite(Sprites.invincibilitySprite);
            }

            if (invisibilityTimer > 14 * Time.deltaTime) {
                invisibilityTimer = 0;
            }

            invisibilityTimer += Time.deltaTime;
        }

        public void idleAnim() {
            if(marioStates.state instanceof States.SmallMario) {
                setSprite(Sprites.smallMarioIdle);
            } else {
                setSprite(Sprites.bigMarioIdle);
            }
        }

        public void jumpAnim() {
            if (marioStates.state instanceof States.SmallMario) {
                setSprite(Sprites.smallMarioJump);
            } else {
                setSprite(Sprites.bigMarioJump);
            }
        }

        public void brakeAnim() {
            if (marioStates.state instanceof States.SmallMario) {
                setSprite(Sprites.smallMarioBrake);
            } else {
                setSprite(Sprites.bigMarioBrake);
            }
        }

        public void crouchAnim() {
            setSprite(Sprites.marioCrouch);
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
            setSprite(Sprites.marioDead);
            if (animTimer > 14 * Time.deltaTime) {
                freezeMovement = false;
            }
        }

        public void growAnim() {
            if (animTimer > (7 * Time.deltaTime) * animFrame) {
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
            if (animTimer > (7 * Time.deltaTime) * animFrame) {
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
    }

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
        String flagPole = "flagPole";
        String win = "win";

        // Mario events
        String shrink = "shrink";
        String grow = "grow";
        String dead = "dead";
        String bigMario = "bigMario";
        String smallMario = "smallMario";
    }

    class States implements Events {
        class IdleState extends State {
            public void onEnter(String event) {
                animation.setAnimation(animation::idleAnim);
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
                    case flagPole:
                        return new FlagPoleState();
                }

                return this;
            }

            public void onEnter(String event) {
                animation.setAnimation(animation::jumpAnim);
                isJumping = true;
                friction = 1;
                
                if (event != Events.falling && event != Events.resumeJump) {
                    vel.y = Config.JUMP_VELOCITY;
                }

                if (event == Events.win) {
                    // When mario jumps off the flagpole
                    vel.y = Config.WIN_JUMP_VELOCITY;
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
                animation.setAnimation(animation::runAnim);
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
                animation.setAnimation(animation::brakeAnim);
                friction = Config.BRAKE_FRICTION;
                acceleration = 0;
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
                animation.setAnimation(animation::runAnim);
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
                animation.setAnimation(animation::crouchAnim);
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
                        animation.setAnimation(animation::growAnim);
                        break;
                    case shrink:
                        animation.setAnimation(animation::shrinkAnim);
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
                            isInvincible = true;
                            break;
                    }
                }
            }
        }

        public class FlagPoleState extends State {
            public State onEvent(String event) {
                if (event.equals(win)) {
                    return new JumpState();
                }

                return this;
            }

            public void onEnter(String event) {
                Keys.freezeInput();
                animation.setAnimation(animation::flagAnim);
                gravity = false;
                vel.y = 0.1f;
            }
            
            public void update() {
                flipSprite = true;
                vel.x = 0;
            }

            public void onExit() {
                vel.x = 0.20f;
                maxVel = vel.x;
                gravity = true;
                winCondition = true;
            }
        }

// MARIO STATES _________________________________________________________________________

        public class SmallMario extends State {
            public State onEvent(String event) {
                switch (event) {
                    case shrink:
                        return new DeadMario();
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
                }

                return this;
            }
        }

        public class DeadMario extends State {
            public void onEnter(String state) {
                actionStates.disable();
                animation.setAnimation(animation::deathAnim);
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
 