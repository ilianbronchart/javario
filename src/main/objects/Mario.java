package src.main.objects;

import java.awt.*;

import src.main.Config;
import src.main.basetypes.*;
import src.main.basetypes.Rectangle;
import src.main.globals.Keys;
import src.main.globals.SpriteAtlas;
import src.main.globals.Time;
import src.main.objects.Mario.States.BigMario;
import src.main.objects.Mario.States.FlagPoleState;

public class Mario extends GameObject {
    private States states = new States();
    private StateMachine actionStates;
    private StateMachine marioStates;
    private Animation animation;

    private boolean isJumping = false;

    private boolean isInvincible = false; // Brief period of invincibility after mario shrinks
    private double invincibilityTimer = 0;

    public Mario(String tag, Rectangle rect){
        super(tag, SpriteAtlas.Mario.smallMarioIdle, rect);
        setSpriteOffset("CenterHorizontal");
        gravity = true;
        isEntity = true;
        animation = new Animation();
        marioStates = new StateMachine(states.new SmallMario());
        actionStates = new StateMachine(states.new IdleState());
        maxVel = Config.MARIO_MAX_VEL;
    }
    
    @Override
    public void update() {
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

    private void handleInvincibility() {
        if (isInvincible) {
            if (invincibilityTimer > 80 * Time.deltaTime) {
                isInvincible = false;
                invincibilityTimer = 0;
            }
            invincibilityTimer += Time.deltaTime;
        }
    }

    private void stateEvents() {
        if (freezeMovement) {  return; }

        if (vel.y == 0) {
            if (Keys.space && !Keys.spacePressed && !isJumping) {
                actionStates.onEvent(Events.jump);
                vel.y = Config.JUMP_VEL;
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
                
                if (Math.abs(vel.x) < Config.MIN_STOP_VEL) {
                    actionStates.onEvent(Events.idle);
                }

                if(Keys.down && marioStates.getState() instanceof BigMario){
                    actionStates.onEvent(Events.crouch);
                }
            }
        } else if (vel.y > 0) {
            // When mario falls off a ledge
            actionStates.onEvent(Events.falling);
        } else if (vel.y < 0) {
            // When mario should be "jumping" but is not in JumpState
            if (!(actionStates.getState() instanceof States.JumpState)) {
                actionStates.onEvent(Events.resumeJump);
            }
        }

        if (rect.pos.y > Config.FRAME_SIZE[1] || ScoreSystem.getTime() == 0) {
            marioStates.onEvent(Events.dead);
        }
    }

    public void onCollision(GameObject col, float dx, float dy) {
        if (col.hasTag(Config.GOOMBA_TAG)) {
            if (rect.pos.y + rect.h - dy * Time.deltaTime < col.rect().pos.y) {
                // Mario is squishing the goomba
                actionStates.onEvent(Events.jump);
                vel.y = Config.ENEMY_SQUISH_JUMP_VEL;
                rect.pos.y = col.rect().pos.y - rect.h;
            } else {
                // Mario is running into the goomba
                if (marioStates.getState() instanceof States.BigMario) {
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

            if (rect.pos.y + rect.h - dy * Time.deltaTime < col.rect().pos.y) {
                // Mario is squishing the turtle
                if (!(turtle.getState() instanceof Turtle.States.ShellState)) {
                    actionStates.onEvent(Events.jump);
                    vel.y = Config.ENEMY_SQUISH_JUMP_VEL;
                }
            } else {
                // Mario is running into the turtle
                if (!(turtle.getState() instanceof Turtle.States.ShellState)) {
                    if (marioStates.getState() instanceof States.BigMario) {
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
            if (marioStates.getState() instanceof States.SmallMario) {
                actionStates.onEvent(Events.grow);
            }

            return;
        }

        if (col.hasTag(Config.FLAGPOLE_TAG)) {
            if (!(actionStates.getState() instanceof States.FlagPoleState)) {
                rect.pos.x = col.rect().pos.x + 30;

                actionStates.onEvent(Events.flagPole);
            }

            if (rect.pos.y + rect.h > col.rect().pos.y + col.rect().h - 20) {
                actionStates.onEvent(Events.jump);
                vel.y = Config.WIN_JUMP_VEL;
            }

            return;
        }

        if (col.hasTag(Config.WIN_TRIGGER_TAG)) {
            actionStates.onEvent(Events.win);
            return;
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
    protected void setSprite(Image newSprite) {
        if (newSprite != sprite) {
            setSpriteOffset("CenterHorizontal");
            setCollider(rect.w, newSprite.getHeight(null));
            sprite = newSprite;
        }
    }

    private class Animation {
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
                if (marioStates.getState() instanceof States.SmallMario) {
                    setSprite(SpriteAtlas.Mario.smallMarioFlag[animFrame % 2]);
                } else {
                    setSprite(SpriteAtlas.Mario.bigMarioFlag[animFrame % 2]);
                }
            }
        }

        public void invincibleAnim() {
            // Alternate between invisible and visible every 7 frames
            if (invisibilityTimer > 7 * Time.deltaTime) {
                setSprite(SpriteAtlas.Mario.invincibilitySprite);
            }

            if (invisibilityTimer > 14 * Time.deltaTime) {
                invisibilityTimer = 0;
            }

            invisibilityTimer += Time.deltaTime;
        }

        public void idleAnim() {
            if(marioStates.getState() instanceof States.SmallMario) {
                setSprite(SpriteAtlas.Mario.smallMarioIdle);
            } else {
                setSprite(SpriteAtlas.Mario.bigMarioIdle);
            }
        }

        public void jumpAnim() {
            if (marioStates.getState() instanceof States.SmallMario) {
                setSprite(SpriteAtlas.Mario.smallMarioJump);
            } else {
                setSprite(SpriteAtlas.Mario.bigMarioJump);
            }
        }

        public void brakeAnim() {
            if (marioStates.getState() instanceof States.SmallMario) {
                setSprite(SpriteAtlas.Mario.smallMarioBrake);
            } else {
                setSprite(SpriteAtlas.Mario.bigMarioBrake);
            }
        }

        public void crouchAnim() {
            setSprite(SpriteAtlas.Mario.marioCrouch);
        }

        public void runAnim() {
            if (marioStates.getState() instanceof States.SmallMario) {
                setSprite(SpriteAtlas.Mario.smallMarioRun[runFrames[animFrame % 4]]);
            } else {
                setSprite(SpriteAtlas.Mario.bigMarioRun[runFrames[animFrame % 4]]);
            }

            if (animTimer > (6 * Time.deltaTime) * animFrame) {
                animFrame++;
            }
        }

        public void deathAnim() {
            setSprite(SpriteAtlas.Mario.marioDead);
            if (animTimer > 14 * Time.deltaTime) {
                freezeMovement = false;
            }
        }

        public void growAnim() {
            if (animTimer > (7 * Time.deltaTime) * animFrame) {
                switch (growFrames[animFrame]) {
                    case 0:
                        setSprite(SpriteAtlas.Mario.smallMarioIdle);
                        break;
                    case 1:
                        setSprite(SpriteAtlas.Mario.mediumMarioIdle);
                        break;
                    case 2:
                        setSprite(SpriteAtlas.Mario.bigMarioIdle);
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
                        setSprite(SpriteAtlas.Mario.smallMarioIdle);
                        break;
                    case 1:
                        setSprite(SpriteAtlas.Mario.mediumMarioIdle);
                        break;
                    case 2:
                        setSprite(SpriteAtlas.Mario.bigMarioIdle);
                        break;
                }

                if (shrinkFrames[animFrame] == 0) {
                    clearAnimation();
                }

                animFrame++;
            }
        }
    }

    // _______________ STATES _______________

    public State getMarioState() {
        return this.marioStates.getState();
    }

    private interface Events {
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

    public class States implements Events {
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
                    case win:
                        return new WinState();
                }
                
                return this;
            }
        }

        public class JumpState extends State {
            // State when jumping when spacebar input affects VEL

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
                acceleration = 0;
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
                        if (!Keys.left && !Keys.right || vel.x == 0) {
                            return new IdleState();
                        }
                        break;
                    case grow: // Fallthrough
                    case shrink:
                        return new PowerupState();
                    case win:
                        return new WinState();
                }
                    
                return this;
            }

            public void onExit() {
                animation.clearAnimation();;
            }
        }

        public class BrakeState extends State {
            // State when input is opposite VEL
            
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
                    // animation.currentSprite = SpriteAtlas.Mario.EMPTY_SPRITE;
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
                if (event.equals(jump)) {
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
                Keys.right = true;
            }
        }

        public class WinState extends State {
            public void onEnter(String event) {
                acceleration = 0;
                vel = new Vector2();
                ScoreSystem.addTimeScore();
            }

            public void update() {
                if (ScoreSystem.getTime() == 0) {
                    triggerScene(Config.Scenes.MAIN_MENU);
                    Keys.unFreezeInput();
                }
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
                vel = new Vector2(0, Config.JUMP_VEL);
                acceleration = 0;
                hasCollider = false;
                freezeMovement = true;
            }

            public void update() {
                if (rect.pos.y > Config.FRAME_SIZE[1] + 1000) {
                    setActive(false);
                    triggerScene(Config.Scenes.MAIN_MENU);
                }
            }
        }
    }
}
