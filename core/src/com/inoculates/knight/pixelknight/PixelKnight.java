
package com.inoculates.knight.pixelknight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.Storage;
import com.inoculates.knight.effects.RollingMud;
import com.inoculates.knight.gems.*;
import com.inoculates.knight.objects.*;
import com.inoculates.knight.platforms.Platform;
import com.inoculates.knight.projectiles.Projectile;
import com.inoculates.knight.enemies.*;
import com.inoculates.knight.screens.GameScreen;

//Pixelknight class responsible for every action of the main character, the pixel knight.

public class PixelKnight extends Sprite implements InputProcessor {
    //All of these static integers represent the various states the pixel knight goes through.
    //Each state the pixel knight is set to will cause the knight to have a different animation.
    public static final int SPAWN = 0;
    public static final int IDLE = 1;
    public static final int ATTACKING = 2;
    public static final int SPECATTACKING = 3;
    public static final int RUNNING = 4;
    public static final int RUNATTACKING = 5;
    public static final int RUNSPECATTACKING = 6;
    public static final int JUMPING = 7;
    public static final int JUMPATTACKING = 8;
    public static final int JUMPSPECATTACKING = 9;
    public static final int FALLING = 10;
    public static final int FALLATTACKING = 11;
    public static final int FALLSPECATTACKING = 12;
    public static final int CLIDLE = 13;
    public static final int CLIMBING = 14;
    public static final int CLATTACKING = 15;
    public static final int CLSPECATTACKING = 16;
    public static final int SWIMMING = 17;
    public static final int SWATTACKING = 18;
    public static final int SWSPECATTACKING = 19;
    public static final int CHARGING = 20;
    public static final int FLYING = 21;
    public static final int REBOUNDING = 22;
    public static final int GLIDING = 23;
    public static final int ROLLING = 24;
    public static final int UNROLLING = 25;
    public static final int DEAD = 26;

    //These are the directions the pixelknight can face, and it affects the orientation of the knight's sprite.
    public static final int LEFT = -1;
    public static final int RIGHT = 1;

    //These two floats are involved in jumping and falling. The JUMP_VELOCITY is the starting jump velocity, while
    //the gravity is the number the velocity of the knight is periodically subtracted by.
    public static final float JUMP_VELOCITY = 2.8f;
    public static final float GRAVITY = 0.11f;

    //These static integers represent the special power the pixel knight holds, and tells the game which power the
    //knight currently has.
    public static final int NOTHING = 0;
    public static final int MUD = 1;
    public static final int CHARGE = 2;
    public static final int GLIDE = 3;
    public static final int HJUMP = 4;
    public static final int ROLL = 5;

    //These are the velocity and acceleration vectors of the knight, which contain both x and y components.
    //They are continually added to either the position vector (in the case of the velocity) and the velocity vector.
    Vector2 vel = new Vector2();
    Vector2 acel = new Vector2();

    //The layer which the pixel knight is drawn on.
    TiledMapTileLayer layer;

    //Animations of the pixel knight, which as mentioned are determined by the state
    Animation idle, run, attack, specAttack, runAttack, runSpecAttack, jump, jumpAttack, jumpSpecAttack,
            fall, fallAttack, fallSpecAttack, climbIdle, climbRun, climbAttack, climbSpecAttack, swimRun, swimAttack,
            swimSpecAttack, charge, roll, unRoll, ball;

    //The screen is the game screen responsible for drawing the knight as well as the world, while the atlas is a
    //reference to all of the knight's textures. The map is the object which contains the information of the pixel
    //knight's surroundings, e.g. the tiles the pixel knight interacts with. Storage is the object that stores
    //persistent data such as the lives and gems the play currently has.
    GameScreen screen;
    TextureAtlas atlas;
    TiledMap map;
    Storage storage;

    //These arrays represent all slopes currently on the map.
    Array<RectangleMapObject> rSlopes = new Array<RectangleMapObject>();
    Array<RectangleMapObject> lSlopes = new Array<RectangleMapObject>();

    //This object handles the knight's powers.
    Powers powers;
    //The timer for the glide power, which needs to expire and renew at certain intervals.
    Timer glideTimer = new Timer();
    //The effect generated by rolling.
    RollingMud rMud;

    //The state of the knight, which determines the current animation.
    int state = SPAWN;
    //The direction of the knight, which determines the orientation. The attackDir supersedes the normal dir in case the
    //pixel knight is attacking. This allows the player to attack and move..
    int dir = LEFT, attackDir;
    int health = 10;
    int armor = 0;
    int dmg = 1;
    public int gems = 0;
    public int lives = 0;
    //What power the knight currently has.
    int power = NOTHING;
    //The animationTime tells the game how far along the knight is in each frame.
    float animationTime = 0;
    //These modifiers influence the velocity of the pixel knight for a short time.
    float modifierX = 0, modifierY = 0;
    //These origins are involved in rotating the sprite, and are used to help determine collision as well.
    float originX, originY;
    //Various booleans which describe the state of the knight.
    public boolean attacking = false;
    public boolean climbable = false;
    public boolean climbing = false;
    public boolean cooldown = false;
    public boolean flickering = false;
    public boolean dJumpable = true;
    public boolean glidable = true;
    public boolean grounded = true;
    public boolean invulnerability = false;
    public boolean onPlatform = false;
    public boolean rolling = false;
    public boolean onSlopeRight = false;
    public boolean onSlopeLeft = false;
    public boolean pX = false;
    public boolean stun = false;
    public boolean swimming = false;
    public boolean transparent = false;
    public boolean unlatchable = false;

    //The regions that serve as frames for the animations of the knight.
    TextureAtlas.AtlasRegion idle1, idle2;
    TextureAtlas.AtlasRegion run1, run2, run3, run4, run5, run6;
    TextureAtlas.AtlasRegion charge1, charge2, charge3, charge4, charge5, charge6;
    TextureAtlas.AtlasRegion rolling1, rolling2, rolling3, rolling4, rolling5, rolling6, rollingball;
    TextureAtlas.AtlasRegion attack1, attack2, attack3, attack4, attack5, attack6, attack7, attack8;
    TextureAtlas.AtlasRegion specAttack1, specAttack2, specAttack3, specAttack4, specAttack5, specAttack6, specAttack7, specAttack8;
    TextureAtlas.AtlasRegion runAttack1, runAttack2, runAttack3, runAttack4;
    TextureAtlas.AtlasRegion runSpecAttack1, runSpecAttack2, runSpecAttack3, runSpecAttack4;
    TextureAtlas.AtlasRegion jump1, jump2, jump3, jump4;
    TextureAtlas.AtlasRegion jumpAttack1, jumpAttack2, jumpAttack3, jumpAttack4;
    TextureAtlas.AtlasRegion jumpSpecAttack1, jumpSpecAttack2, jumpSpecAttack3, jumpSpecAttack4;
    TextureAtlas.AtlasRegion fall1, fall2, fall3, fall4;
    TextureAtlas.AtlasRegion fallAttack1, fallAttack2, fallAttack3, fallAttack4;
    TextureAtlas.AtlasRegion fallSpecAttack1, fallSpecAttack2, fallSpecAttack3, fallSpecAttack4;
    TextureAtlas.AtlasRegion climbingIdle1, climbingIdle2, climbing1, climbing2;
    TextureAtlas.AtlasRegion climbingAttack1, climbingAttack2, climbingAttack3, climbingAttack4;
    TextureAtlas.AtlasRegion climbingSpecAttack1, climbingSpecAttack2, climbingSpecAttack3, climbingSpecAttack4;
    TextureAtlas.AtlasRegion swimming1, swimming2, swimming3, swimming4;
    TextureAtlas.AtlasRegion swimmingAttack1, swimmingAttack2, swimmingAttack3, swimmingAttack4;
    TextureAtlas.AtlasRegion swimmingSpecAttack1, swimmingSpecAttack2, swimmingSpecAttack3, swimmingSpecAttack4;

    //Sounds for the various actions of the pixel knight.
    private Sound attackS1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Attack.wav")),
    attackS2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Attack2.wav")), attackS3 =
            Gdx.audio.newSound(Gdx.files.internal("data/sounds/Attack3.wav"));

    private Sound throwMud = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Laser_Shoot5.wav"));

    private Sound hurtS1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Hit_Hurt.wav")),
            hurtS2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Hit_Hurt2.wav")),
            hurtS3 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Hit_Hurt.wav"));

    private Sound jumpS1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Jump.wav")),
            jumpS2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Jump2.wav")),
            jumpS3 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Jump3.wav"));

    private Sound gemS1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Pickup_Coin.wav")),
            gemS2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Pickup_Coin2.wav")),
            gemS3 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Pickup_Coin3.wav"));

    private Sound pwrUpS1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Powerup.wav")),
            pwrUpS2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Powerup2.wav")),
            pwrUpS3 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Powerup3.wav"));

    private Sound death = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Explosion4.wav"));

    //Initializes the pixel knight class by setting the knight's atlas, layer, screen, map, frames, and animations.
    public PixelKnight(TextureAtlas atlas, TiledMapTileLayer layer, GameScreen screen, TiledMap map) {
        super(atlas.findRegion("idle1"));
        this.atlas = atlas;
        this.layer = layer;
        this.screen = screen;
        setState(SPAWN, true);
        setSize(14.4f, 25.6f);
        storage = screen.storage;
        this.map = map;
        powers = new Powers(this, screen, layer, atlas);
        gems = storage.gems;
        lives = storage.lives;
        createRegions();
        createAnimations();
        originX = getOriginX();
        originY = getOriginY();
    }

    //This method interprets any commands coming out a trackpad or a mouse and translates it into action depending
    //on the user's control settings. The actual commands are explained in the keyDown method.
    public boolean touchDown(int x, int y, int pointer, int button) {
        if (button == storage.attack && !rolling && state != CHARGING)
            attack();
        if (button == storage.moveUp && (!stun || state == CHARGING)) {
            if (screen.exitLevel())
                stun = true;
            else if (climbable && !rolling && !stun)
                climbing = true;
            else jump();
        }
        if (button == storage.moveDown) {
            transparent = true;
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    transparent = false;
                }
            }, 0.4f);
            timer.start();
            grounded = false;
        }
        if (button == storage.specAttack && state != ATTACKING && state != SPECATTACKING) {
            specDecider(Gdx.input.getX(), Gdx.input.getY());
        }
        return true;
    }

    public boolean touchDragged(int x, int y, int z) {
        return true;
    }

    public boolean keyTyped(char x) {
        return true;
    }

    public boolean scrolled(int x) {
        return true;
    }

    //Interprets the users input in the form of keys pressed and translates it to actions.
    public boolean keyDown(int x) {
        //User pressed the up key.
        if (x == storage.moveUp && (!stun || state == CHARGING)) {
            //Checks if the user is at the end door.
            if (screen.exitLevel())
                stun = true;
            //Checks if the user can climb.
            else if (climbable && !rolling && !stun)
                climbing = true;
            //Otherwise makes the pixel knight jump.
            else jump();
        }
        //User pressed the down key. This phases the knight through any platforms.
        if (x == storage.moveDown) {
            transparent = true;
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    transparent = false;
                }
            }, 0.4f);
            timer.start();
            grounded = false;
        }
        //User pressed the special attack key, launching the specdecider method.
        if (x == storage.specAttack && state != ATTACKING && state != SPECATTACKING) {
            specDecider(Gdx.input.getX(), Gdx.input.getY());
        }
        //User pressed the lose power key, getting rid of all powers and playing sound.
        if (x == storage.losePower && power != NOTHING) {
            Array<Sound> sounds = new Array<Sound>();
            sounds.addAll(pwrUpS1, pwrUpS2, pwrUpS3);
            playRandomSound(sounds);
            setColor(Color.luminanceAlpha(500, 255));
            power = NOTHING;
            depower();
            screen.powerUI.update(power);
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    setColor(Color.WHITE);
                }
            }, 0.8f);
            timer.start();
        }
        if (x == storage.attack && !rolling && state != CHARGING)
            attack();
        return true;
    }

    public boolean mouseMoved(int x, int y) {
        return true;
    }

    public boolean keyUp(int x) {
        return true;
    }

    public boolean touchUp(int x, int y, int z, int a) {
        return true;
    }

    //Method responsible for periodically making the knight act.
    private void update(float deltaTime) {
        updateTime(deltaTime);
        chooseSprite();
        handleEvents();
        processKeys();
        tryMove();
        detectBalls();
        detectEnemies();
        detectGems();
        detectSpikers();
        canClimb();
        collidesSlopeLeft();
        collidesSlopeRight();
        storage.setGems(gems);
        storage.setLives(lives);
    }

    //This method moves the pixel knight through both x and y velocities.
    private void tryMove() {
        if (vel.x == 0 && vel.y == 0)
            return;
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false, collisionP = false;

        //Adds velocity to x value.
        setX(getX() + vel.x);

        //Detects collision and if there is one, moves the knight back.
        if (vel.x < 0)
            collisionX = collidesLeft() || collidesDestructibleLeft();
        else if (vel.x > 0)
            collisionX = collidesRight() || collidesDestructibleRight();

        if (collisionX && !onSlopeLeft && !onSlopeRight && !onSlope()) {
            if (state == CHARGING || state == FLYING)
                rebound();
            else {
                setX(oldX);
                vel.x = 0;
                acel.x = 0;
            }
        }

        //Adds velocity to y value.
        setY(getY() + vel.y);

        //Detects collision on the y axis.
        if (vel.y < 0) {
            collisionY = collidesBottom() || collidesDestructibleBottom();
            if (collisionY)
                climbing = false;
            collisionP = collidesPlatform();
            if (!collisionY && !collisionP && !onPlatform && !onSlopeRight && !onSlopeLeft) {
                if (grounded) {
                    Timer timer = new Timer();
                    timer.scheduleTask(new Timer.Task() {
                        @Override
                        public void run() {
                            final boolean collision = collidesBottom() || collidesPlatform();
                            if (!collision && !onPlatform && !onSlopeRight && !onSlopeLeft)
                                grounded = false;
                        }
                    }, 0.15f);
                    timer.start();
                }
            }
            else
                grounded = true;
            if (state == JUMPING || state == CLIMBING)
            setState(FALLING, true);
            else if (!grounded)
            setState(FALLING, false);
        }

        else if (vel.y > 0) {
            collisionY = collidesTop() || collidesDestructibleTop();
            setState(JUMPING, false);
            grounded = false;
        }

        if ((collisionY || collisionP) && !onSlopeRight && !onSlopeLeft) {
            if (!collisionP)
                setY(oldY);
            if (state == REBOUNDING) {
                setState(IDLE, true);
                vel.x = 0;
                vel.y = 0;
                unStun();
            }
            if (state == FLYING) {
                brake();
                vel.y = 0;
            }
            vel.y = 0;
            glidable = true;
            onPlatform = false;
            transparent = false;
        }
    }

    //Method responsible for making the knight jump.
    private void jump() {
        //Plays the jump sound.
        Array<Sound> sounds = new Array<Sound>();
        sounds.addAll(jumpS1, jumpS2, jumpS3);
        if (rolling) return;
        //Jumps if on the ground.
        if (grounded) {
            setState(JUMPING, false);
            playRandomSound(sounds);
            if (onPlatform) {
                unlatchable = true;
                Timer timer = new Timer();
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        unlatchable = false;
                    }
                }, 0.1f);
                timer.start();
            }
            if (power == HJUMP) {
                if (onPlatform)
                    vel.y = JUMP_VELOCITY * 1.5f;
                else SVY(JUMP_VELOCITY * 1.5f);
            }
            else {
                if (onPlatform)
                    vel.y = JUMP_VELOCITY;
                else SVY(JUMP_VELOCITY);
            }
            onPlatform = false;
            pX = false;
            grounded = false;
            dJumpable = true;
            glidable = true;
        }
        //Double jumps if able to.
        else if (dJumpable && (state == JUMPING || state == FALLING || state == ATTACKING)) {
            playRandomSound(sounds);
            dJumpable = false;
            transparent = false;
            if (power == HJUMP) SVY(JUMP_VELOCITY);
            else SVY(JUMP_VELOCITY / 1.5f);
        }
    }

    //Glides if able to, then falls afterwards.
    private void glide() {
        vel.y = 0;
        setState(GLIDING, true);
        glideTimer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    if (state == GLIDING) {
                        glidable = false;
                        setState(FALLING, true);
                    }
                }
            }, 0.75f);
            glideTimer.start();
    }

    //This large method is used to move the pixel knight by a constant rate as opposed to jumping.
    private void processKeys() {
        //This alters the y velocity if the knight is climbing.
        if (climbing && state != ATTACKING) {
            if (vel.y == 0 && vel.x == 0)
            setState(CLIDLE, true);
            else setState(CLIMBING, true);
            dJumpable = true;
            glidable = true;
            if (Gdx.input.isKeyPressed(storage.moveUp) && detectClimbing(getX(), getY() + 11)) {
                SVY(0.9f);
            } else if (Gdx.input.isKeyPressed(storage.moveDown)) {
                SVY(-0.9f);
            }
            else vel.y = 0;
        }

        //This handles swimming by slightly increasing and decreasing the velocities.
        if (swimming && state != CHARGING) {
            unStun();
            setState(SWIMMING, true);
            if (vel.x > 0)
                vel.x -= 0.01;
            if (vel.x < 0)
                vel.x += 0.01;
            if (vel.y > 0)
                vel.y -= 0.01;
            if (vel.y < 0)
                vel.y += 0.01;
            if (vel.y < 0.01 && vel.y > -0.01)
                vel.y = 0;
            if (vel.x < 0.01 && vel.x > -0.01)
                vel.x = 0;
            if (Gdx.input.isKeyPressed(storage.moveUp))
                SVY(0.6f);
            else if (Gdx.input.isKeyPressed(storage.moveDown))
                SVY(-0.6f);
        }

        //Handles gliding.
        if (Gdx.input.isKeyPressed(storage.moveUp)) {
            if (power == GLIDE && glidable && !dJumpable && vel.y <= 0)
                glide();
        }
        else if (state == GLIDING) {
            glidable = false;
            setState(FALLING, true);
            glideTimer.clear();
        }

        //This moves the pixel knight to the left using velocities. The amount differs based on whether the knight is
        //swimming or climbing, or simply running.
        if (Gdx.input.isKeyPressed(storage.moveLeft)) {
            if (stun)
                return;

            dir = LEFT;
            if (!attacking)
                attackDir = dir;

            if (state != JUMPING && grounded)
                setState(RUNNING, false);

             if (state != FLYING && vel.x != 0 && Math.signum(vel.x) == dir && pX && !rolling)
                vel.x = (1.5f + Math.abs(vel.x)) * dir;
             else if (state != FLYING && !rolling)
                 SVX(1.5f);
             if (climbing)
                 SVX(0.9f);
             else if (swimming)
                 SVX(0.6f);
             if (rolling)
                 acel.x = -0.05f;
        }

        //Same as the left method, except for right velocities.
        else if (Gdx.input.isKeyPressed(storage.moveRight)) {
            if (stun)
                return;

            dir = RIGHT;

            if (!attacking)
                attackDir = dir;

            if (state != JUMPING && grounded)
                setState(RUNNING, false);

                if (state != FLYING && vel.x != 0 && Math.signum(vel.x) == dir && pX && !rolling)
                    vel.x = (1.5f + Math.abs(vel.x)) * dir;
                else if (state != FLYING && !rolling)
                    SVX(1.5f);
                if (climbing)
                    SVX(0.9f);
                else if (swimming)
                    SVX(0.6f);
                if (rolling)
                    acel.x = 0.05f;
        }

        //This halts the knight if no buttons are currently being pressed.
        else {
            if (!stun && !pX && !swimming && !rolling)
                vel.x = 0;
            if (rolling)
                acel.x = 0;
            if (grounded)
                setState(IDLE, false);
        }
    }

    //This draws the knight based upon the direction te knight is facing.
    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());

        if (attackDir == LEFT && attacking) {
            setScale(-1, 1);
        }
        if (attackDir == LEFT && state != IDLE && state != CLIDLE && state != CLIMBING && !attacking) {
            setFlip(true, false);
            setScale(1, 1);
        }
        if (attackDir == RIGHT) {
            setFlip(false, false);
            setScale(1, 1);
        }

        super.draw(batch);
    }

    //Checks whether the cell at the x and y point is blocked or not.
    private boolean isCellBlocked(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && (cell.getTile().getProperties().containsKey("blocked"));
}

    //Checks whether the cell at the x and y point is a log or not.
    private boolean isCellLog(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("log") && onTop(cell.getTile().getOffsetY() - 2)) {
            setY((int) (getY() / layer.getTileHeight() + 1) * layer.getTileHeight() - 2);
            return true;
        }
        return false;
    }

    //Checks whether the cell at the x and y point is a spike or not.
    private boolean isCellSpike(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("spike");
    }

    //Checks whether the cell at the x and y point is able to be climbed by the knight or not.
    private boolean isCellClimbable(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("climbable");
    }

    //Checks whether the cell at the x and y point is able to be swum in or not.
    private boolean isCellSwimmable(float x, float y) {
        TiledMapTileLayer background = (TiledMapTileLayer) map.getLayers().get(1);
        TiledMapTileLayer.Cell cell = background.getCell(((int) (x / layer.getTileWidth())), (int) (y / layer.getTileHeight()));
        return cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("water");
    }

    //Checks whether the cell at the x and y point is a platform or not. If the knight is on a platform, the game will
    //set the knight on top of the platform
    private boolean isCellPlatform(float x, float y) {
        TiledMapTileLayer.Cell cell = layer.getCell((int) (x / layer.getTileWidth()), (int) (y / layer.getTileHeight()));
        if (cell != null && cell.getTile() != null && cell.getTile().getProperties().containsKey("platform")
                && onTop(cell.getTile().getOffsetY()) && !Gdx.input.isKeyPressed(storage.moveDown) && !transparent)
        {
            setY((int) (getY() / layer.getTileHeight() + 1) * layer.getTileHeight());
            return true;
        }
        else return false;
    }

    //This periodically checks whether any part of the knight is in a blocked cell if his x velocity is positive.
    public boolean collidesRight() {
        for (float step = 0; step < getHeight(); step += layer.getTileHeight() / 16)
            for (float x = 0; x < getWidth(); x += layer.getTileHeight() / 16)
                if (isCellBlocked(getPosX() + x, getY() + step))
                    return true;
        return false;
    }

    //This periodically checks whether any part of the knight is a destructible if x his velocity is positive.
    public boolean collidesDestructibleRight() {
        for (Destructible destructible : screen.destructibles)
            for (float step = 0; step < getHeight(); step += layer.getTileHeight() / 16)
                for (float x = 0; x < getWidth(); x += layer.getTileWidth() / 16)
                    if (getPosX() + x > destructible.getX() && getPosX() + x < destructible.getX() + destructible.getWidth() &&
                        getY() + step > destructible.getY() && getY() + step < destructible.getY() + destructible.getHeight()) {
                        if (state == CHARGING) {
                            if (destructible instanceof Box || destructible instanceof BoxLarge)
                                rebound();
                            destructible.explode(this);
                        }
                        if (destructible instanceof Spiker) {
                            Spiker spiker = (Spiker) destructible;
                            if (spiker.isSolid(getPosX() + getWidth(), getY() + step))
                                return true;
                        }
                        if (destructible.isTangible())
                            return true;
                    }
        return false;
    }

    //This periodically checks whether any part of the knight is in a blocked cell if his x velocity is negative.
    public boolean collidesLeft() {
        for (float step = 0; step < getHeight(); step += layer.getTileHeight() / 16)
            for (float x = 0; x < getWidth() / 2; x += layer.getTileHeight() / 16)
                if (isCellBlocked(getPosX() + x, getY() + step))
                return true;
        return false;
    }

    //This periodically checks whether any part of the knight is a destructible if his x velocity is negative.
    public boolean collidesDestructibleLeft() {
        for (Destructible destructible : screen.destructibles)
            for (float step = 0; step < getHeight(); step += layer.getTileHeight() / 16)
                for (float x = 0; x < getWidth(); x += layer.getTileWidth() / 16)
                    if (getPosX() + x> destructible.getX() && getPosX() + x < destructible.getX() + destructible.getWidth() &&
                        getY() + step > destructible.getY() && getY() + step < destructible.getY() + destructible.getHeight()) {
                        if (state == CHARGING) {
                            if (destructible instanceof Box || destructible instanceof BoxLarge)
                                rebound();
                            destructible.explode(this);
                        }
                        if (destructible instanceof Spiker) {
                            Spiker spiker = (Spiker) destructible;
                            if (spiker.isSolid(getPosX(), getY() + step))
                                return true;
                        }
                        if (destructible.isTangible())
                            return true;
                    }
        return false;
    }

    //This periodically checks whether any part of the knight is in a blocked cell if his y velocity is positive.
    public boolean collidesTop() {
        for (float step = 0; step < getWidth() - 3; step += layer.getTileWidth() / 16)
            if (isCellBlocked(getPosX() + step, getY() + getHeight()))
                return true;
        return false;
    }

    //This periodically checks whether any part of the knight is a destructible if his y velocity is positive.
    public boolean collidesDestructibleTop() {
        for (Destructible destructible : screen.destructibles)
            for (float step = 0; step < getWidth(); step += layer.getTileWidth() / 16)
                if (getPosX() + step > destructible.getX() && getPosX() + step < destructible.getX() + destructible.getWidth() &&
                        getY() + getHeight() > destructible.getY() && getY() + getHeight() < destructible.getY() + destructible.getHeight()) {
                    if (destructible instanceof Spiker) {
                        Spiker spiker = (Spiker) destructible;
                        if (spiker.isSolid(getPosX() + step, getY() + getHeight()))
                            return true;
                        }
                    if (destructible.isTangible())
                        return true;
                }
        return false;
    }

    //This periodically checks whether any part of the knight is in a blocked cell if his y velocity is negative.
    public boolean collidesBottom() {
        for (float step = 0; step < getWidth() - 3; step += layer.getTileWidth() / 16) {
            if (isCellBlocked(getPosX() + step, getY()))
                return true;
            if (isCellLog(getPosX() + step, getY() + 3))
                return true;

        }
        return false;
    }

    //This periodically checks whether any part of the knight is a destructible if his y velocity is negative.
    public boolean collidesDestructibleBottom() {
        for (Destructible destructible : screen.destructibles)
            for (float step = 0; step < getWidth(); step += layer.getTileWidth() / 16)
                if (getPosX() + step > destructible.getX() && getPosX() + step < destructible.getX() + destructible.getWidth() &&
                        getY() > destructible.getY() && getY() < destructible.getY() + destructible.getHeight()) {
                    if (destructible instanceof Spiker) {
                        Spiker spiker = (Spiker) destructible;
                        if (spiker.isSolid(getPosX() + step, getY()))
                            return true;
                    }
                    if (destructible instanceof Raft || destructible instanceof Cart) {
                        if (destructible.vel.x != 0) {
                            vel.x = destructible.vel.x;
                            pX = true;
                        }
                        else pX = false;
                    }
                    if (destructible.isTangible())
                        return true;
                }
        return false;
    }

    //This periodically checks whether the knight is on a platform.
    public boolean collidesPlatform() {
        for (float step = 0; step < getWidth(); step += layer.getTileWidth() / 16)
            if (isCellPlatform(getPosX() + step, getY())) {
                return true;
            }
        return false;
    }

    //This periodically checks whether the knight is on a right slope, and adjusts his position accordingly.
    public boolean collidesSlopeRight() {
        if (rSlopes == null) return false;
        for (RectangleMapObject rectMapObject : rSlopes) {
            Rectangle rect = rectMapObject.getRectangle();
            float rWidth = (int) (rect.getWidth() / layer.getTileWidth()) * layer.getTileWidth();
            float rHeight = (int) (rect.getHeight() / layer.getTileHeight()) * layer.getTileHeight();
            float rX = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() + getWidth() / 2;
            float rY = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
            if (getPosX() + getWidth() / 1.2f > rX && getPosX() < rX + rWidth) {
                for (float i = rWidth; i > 0; i -= layer.getTileWidth() / 10000) {
                    if (getPosX() + getWidth() > rX + i && getY() < rY + i && getPosX() + getWidth() < rX + rWidth && getY() >= rY) {
                        onSlopeRight = true;
                        setY(rY + i);
                        if (state == REBOUNDING) {
                            setState(IDLE, true);
                            vel.x = 0;
                            vel.y = 0;
                            unStun();
                        }
                        if (state == FLYING) {
                            brake();
                            vel.y = 0;
                        }
                        if (state == CLIMBING || state == FALLING)
                            setState(IDLE, true);
                        return true;
                    }
                    if (onSlopeRight && getPosX() + getWidth() > rX + rWidth - 1.5f) {
                        setY(rY + rHeight);
                        if (rolling)
                            vel.y = vel.x;
                    }
                }
            }
        }
        onSlopeRight = false;
        return false;
    }

    //This periodically checks whether the knight is on a left slope, and adjusts his position accordingly.
    public boolean collidesSlopeLeft() {
        if (lSlopes == null)
            return false;
        for (RectangleMapObject rectMapObject : lSlopes) {
            Rectangle rect = rectMapObject.getRectangle();
            float rWidth = (int) (rect.getWidth() / layer.getTileWidth()) * layer.getTileWidth();
            float rHeight = (int) (rect.getHeight() / layer.getTileHeight()) * layer.getTileHeight();
            float rX = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() - getWidth() / 2;
            float rY = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
            if (getPosX() + getWidth() > rX && getPosX() + getWidth() / 4 < rX + rWidth) {
                for (float i = 0; i < rWidth; i += layer.getTileWidth() / 10000) {
                    if (getPosX() < rX + i && getY() < rY + rHeight - i && getPosX() > rX && getY() >= rY) {
                        onSlopeLeft = true;
                        setY(rY + rHeight - i);
                        if (state == REBOUNDING) {
                            setState(IDLE, true);
                            vel.x = 0;
                            vel.y = 0;
                            unStun();
                        }
                        if (state == FLYING) {
                            brake();
                            vel.y = 0;
                        }
                        if (state == CLIMBING || state == FALLING)
                            setState(IDLE, true);
                        return true;
                    }
                    if (onSlopeLeft && getPosX() < rX + 1.5f) {
                        setY(rY + rHeight);
                        if (rolling)
                            vel.y = -vel.x;
                    }
                }
            }
        }
        onSlopeLeft = false;
        return false;
    }

    //This boolean is used to disable horizontal collision while the knight is on a slope.
    private boolean onSlope() {
        if (rSlopes == null)
            return false;
        for (RectangleMapObject rectMapObject : rSlopes) {
            Rectangle rect = rectMapObject.getRectangle();
            float rWidth = (int) (rect.getWidth() / layer.getTileWidth()) * layer.getTileWidth();
            float rHeight = (int) (rect.getHeight() / layer.getTileHeight()) * layer.getTileHeight();
            float rX = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth();
            float rY = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
            if (getPosX() > rX - getWidth() / 2 && getPosX() < rX + rWidth && getY() < rY + rHeight && getY() > rY)
                return true;
            if (rolling && getPosX() + getWidth() / 2 > rX && getPosX() + getWidth() / 2 < rX + rWidth && getY() < rY + rHeight && getY() >= rY)
                return true;
        }
        if (lSlopes == null)
            return false;
        for (RectangleMapObject rectMapObject : lSlopes) {
            Rectangle rect = rectMapObject.getRectangle();
            float rWidth = (int) (rect.getWidth() / layer.getTileWidth()) * layer.getTileWidth();
            float rHeight = (int) (rect.getHeight() / layer.getTileHeight()) * layer.getTileHeight();
            float rX = (int) (rect.getX() / layer.getTileWidth()) * layer.getTileWidth() - getWidth() / 2;
            float rY = (int) (rect.getY() / layer.getTileHeight()) * layer.getTileHeight();
            if (getPosX() > rX && getPosX() < rX + rWidth && getY() < rY + rHeight && getY() >= rY)
                return true;
        }
        return false;
    }

    //This determines the tile height of the platform, which is used for the placement of the pixel knight.
    private boolean onTop(float h)
    {
        float c = (getY() / layer.getTileHeight() - h / layer.getTileHeight());
        for (int i = 0; i < 50; i ++)
            if (i - c < 0.22f && i - c > 0)
                return true;
        return false;
    }

    //Creates all the animations of the knight with their corresponding frames.
    private void createAnimations() {
        idle = new Animation(0.5f, idle1, idle2);
        run = new Animation(0.16666667f, run1, run2, run3, run4, run5, run6);
        charge = new Animation(0.16666667f, charge1, charge2, charge3, charge4, charge5, charge6);
        roll = new Animation(0.2f, rolling1, rolling2, rolling3, rolling4, rolling5, rolling6);
        unRoll = new Animation(0.2f, rolling6, rolling5, rolling4, rolling3, rolling2, rolling1);
        ball = new Animation(1, rollingball);
        attack = new Animation(0.0675f, attack1, attack2, attack3, attack4, attack5, attack6, attack7, attack8);
        specAttack = new Animation(0.0675f, specAttack1, specAttack2, specAttack3, specAttack4, specAttack5, specAttack6, specAttack7, specAttack8);
        runAttack = new Animation(0.135f, runAttack1, runAttack2, runAttack3, runAttack4);
        runSpecAttack = new Animation(0.135f, runSpecAttack1, runSpecAttack2, runSpecAttack3, runSpecAttack4);
        jump = new Animation(0.25f, jump1, jump2, jump3, jump4);
        jumpAttack = new Animation(0.135f, jumpAttack1, jumpAttack2, jumpAttack3, jumpAttack4);
        jumpSpecAttack = new Animation(0.135f, jumpSpecAttack1, jumpSpecAttack2, jumpSpecAttack3, jumpSpecAttack4);
        fall = new Animation(0.25f, fall1, fall2, fall3, fall4);
        fallAttack = new Animation(0.135f, fallAttack1, fallAttack2, fallAttack3, fallAttack4);
        fallSpecAttack = new Animation(0.135f, fallSpecAttack1, fallSpecAttack2, fallSpecAttack3, fallSpecAttack4);
        climbIdle = new Animation(0.5f, climbingIdle1, climbingIdle2);
        climbRun = new Animation(0.2f, climbing1, climbing2);
        climbAttack = new Animation(0.135f, climbingAttack1, climbingAttack2, climbingAttack3, climbingAttack4);
        climbSpecAttack = new Animation(0.135f, climbingSpecAttack1, climbingSpecAttack2, climbingSpecAttack3, climbingSpecAttack4);
        swimRun = new Animation(0.166666667f, swimming1, swimming2, swimming3, swimming4, swimming3, swimming2);
        swimAttack = new Animation(0.135f, swimmingAttack1, swimmingAttack2, swimmingAttack3, swimmingAttack4);
        swimSpecAttack = new Animation(0.135f, swimmingSpecAttack1, swimmingSpecAttack2, swimmingSpecAttack3, swimmingSpecAttack4);
    }

    //Creates all frames used in animations in the form of atlas regions.
    private void createRegions() {
        idle1 = atlas.findRegion("idle1");
        idle2 = atlas.findRegion("idle2");

        run1 = atlas.findRegion("run1");
        run2 = atlas.findRegion("run2");
        run3 = atlas.findRegion("run3");
        run4 = atlas.findRegion("run4");
        run5 = atlas.findRegion("run5");
        run6 = atlas.findRegion("run6");

        attack1 = atlas.findRegion("attack1");
        attack2 = atlas.findRegion("attack2");
        attack3 = atlas.findRegion("attack3");
        attack4 = atlas.findRegion("attack4");
        attack5 = atlas.findRegion("attack5");
        attack6 = atlas.findRegion("attack6");
        attack7 = atlas.findRegion("attack7");
        attack8 = atlas.findRegion("attack8");

        specAttack1 = atlas.findRegion("specattack1");
        specAttack2 = atlas.findRegion("specattack2");
        specAttack3 = atlas.findRegion("specattack3");
        specAttack4 = atlas.findRegion("specattack4");
        specAttack5 = atlas.findRegion("specattack5");
        specAttack6 = atlas.findRegion("specattack6");
        specAttack7 = atlas.findRegion("specattack7");
        specAttack8 = atlas.findRegion("specattack8");

        runAttack1 = atlas.findRegion("runattack1");
        runAttack2 = atlas.findRegion("runattack2");
        runAttack3 = atlas.findRegion("runattack3");
        runAttack4 = atlas.findRegion("runattack4");

        runSpecAttack1 = atlas.findRegion("runspecattack1");
        runSpecAttack2 = atlas.findRegion("runspecattack2");
        runSpecAttack3 = atlas.findRegion("runspecattack3");
        runSpecAttack4 = atlas.findRegion("runspecattack4");

        jump1 = atlas.findRegion("jump1");
        jump2 = atlas.findRegion("jump2");
        jump3 = atlas.findRegion("jump3");
        jump4 = atlas.findRegion("jump4");

        jumpAttack1 = atlas.findRegion("jumpattack1");
        jumpAttack2 = atlas.findRegion("jumpattack2");
        jumpAttack3 = atlas.findRegion("jumpattack3");
        jumpAttack4 = atlas.findRegion("jumpattack4");

        jumpSpecAttack1 = atlas.findRegion("jumpspecattack1");
        jumpSpecAttack2 = atlas.findRegion("jumpspecattack2");
        jumpSpecAttack3 = atlas.findRegion("jumpspecattack3");
        jumpSpecAttack4 = atlas.findRegion("jumpspecattack4");

        fall1 = atlas.findRegion("falling1");
        fall2 = atlas.findRegion("falling2");
        fall3 = atlas.findRegion("falling3");
        fall4 = atlas.findRegion("falling4");

        fallAttack1 = atlas.findRegion("fallingattack1");
        fallAttack2 = atlas.findRegion("fallingattack2");
        fallAttack3 = atlas.findRegion("fallingattack3");
        fallAttack4 = atlas.findRegion("fallingattack4");

        fallSpecAttack1 = atlas.findRegion("fallingspecattack1");
        fallSpecAttack2 = atlas.findRegion("fallingspecattack2");
        fallSpecAttack3 = atlas.findRegion("fallingspecattack3");
        fallSpecAttack4 = atlas.findRegion("fallingspecattack4");

        climbingIdle1 = atlas.findRegion("climbingidle1");
        climbingIdle2 = atlas.findRegion("climbingidle2");
        climbing1 = atlas.findRegion("climbing1");
        climbing2 = atlas.findRegion("climbing2");

        climbingAttack1 = atlas.findRegion("climbingattack1");
        climbingAttack2 = atlas.findRegion("climbingattack2");
        climbingAttack3 = atlas.findRegion("climbingattack3");
        climbingAttack4 = atlas.findRegion("climbingattack4");

        climbingSpecAttack1= atlas.findRegion("climbingspecattack1");
        climbingSpecAttack2= atlas.findRegion("climbingspecattack2");
        climbingSpecAttack3= atlas.findRegion("climbingspecattack3");
        climbingSpecAttack4= atlas.findRegion("climbingspecattack4");

        swimming1 = atlas.findRegion("swimming1");
        swimming2 = atlas.findRegion("swimming2");
        swimming3 = atlas.findRegion("swimming3");
        swimming4 = atlas.findRegion("swimming4");

        swimmingAttack1 = atlas.findRegion("swattack1");
        swimmingAttack2 = atlas.findRegion("swattack2");
        swimmingAttack3 = atlas.findRegion("swattack3");
        swimmingAttack4 = atlas.findRegion("swattack4");

        swimmingSpecAttack1 = atlas.findRegion("swspecattack1");
        swimmingSpecAttack2 = atlas.findRegion("swspecattack2");
        swimmingSpecAttack3 = atlas.findRegion("swspecattack3");
        swimmingSpecAttack4 = atlas.findRegion("swspecattack4");

        charge1 = atlas.findRegion("charge1");
        charge2 = atlas.findRegion("charge2");
        charge3 = atlas.findRegion("charge3");
        charge4 = atlas.findRegion("charge4");
        charge5 = atlas.findRegion("charge5");
        charge6 = atlas.findRegion("charge6");

        rolling1 = atlas.findRegion("rolling1");
        rolling2 = atlas.findRegion("rolling2");
        rolling3 = atlas.findRegion("rolling3");
        rolling4 = atlas.findRegion("rolling4");
        rolling5 = atlas.findRegion("rolling5");
        rolling6 = atlas.findRegion("rolling6");

        rollingball = atlas.findRegion("rollingball");
    }

    //This method sets the state of the pixel knight, checking whether or not the state should override the
    //current one.
    private void setState(int state, boolean override)
    {
        if ((((this.state == ATTACKING || this.state == RUNATTACKING || this.state == JUMPATTACKING ||
                this.state == FALLATTACKING || this.state == CLATTACKING || this.state == SWATTACKING ||
                this.state == SPECATTACKING || this.state == RUNSPECATTACKING || this.state == JUMPSPECATTACKING ||
                this.state == FALLSPECATTACKING || this.state == CLSPECATTACKING || this.state == SWSPECATTACKING ) && attacking)
                || this.state == CHARGING || this.state == REBOUNDING || this.state == FLYING || this.state == JUMPING ||
                (this.state == CLIMBING || this.state == CLIDLE  && climbing) || (this.state == SWIMMING && swimming) ||
                this.state == GLIDING || this.state == ROLLING || this.state == UNROLLING || this.state == DEAD) && !override)
            return;

        if ((override && !priorities(state)) || this.state == state)
            return;

        this.state = state;
        animationTime = 0;
    }

    //Overrides the current state if necessary.
    private boolean priorities(int cState) {
        switch (cState) {
            case FALLING:
                if (climbing)
                    return false;
            case JUMPING:
                if (climbing || state == REBOUNDING)
                    return false;
            case CHARGING:
                if (swimming)
                    return false;
            case CLIDLE:
                if (attacking)
                    return false;
            case CLIMBING:
                if (attacking)
                    return false;
            case SWIMMING:
                if (attacking)
                    return false;
        }
        return true;
    }

    //This method periodically sets the frame of the pixelknight dependent on both the state and the animationTime.
    private void chooseSprite()
    {
        Animation anim = idle;
        if (state == IDLE || state == DEAD) {
            setRegion(idle.getKeyFrame(animationTime, true));
            anim = idle;
        }
        if (state == RUNNING) {
            setRegion(run.getKeyFrame(animationTime, true));
            anim = run;
        }
        if (state == RUNATTACKING) {
            setRegion(runAttack.getKeyFrame(animationTime, true));
            anim = runAttack;
        }
        if (state == RUNSPECATTACKING) {
            setRegion(runSpecAttack.getKeyFrame(animationTime, true));
            anim = runSpecAttack;
        }
        if (state == ATTACKING) {
            setRegion(attack.getKeyFrame(animationTime, true));
            anim = attack;
        }
        if (state == SPECATTACKING) {
            setRegion(specAttack.getKeyFrame(animationTime, true));
            anim = specAttack;
        }
        if (state == JUMPING) {
            setRegion(jump.getKeyFrame(animationTime, true));
            anim = jump;
        }
        if (state == JUMPATTACKING) {
            setRegion(jumpAttack.getKeyFrame(animationTime, true));
            anim = jumpAttack;
        }
        if (state == JUMPSPECATTACKING) {
            setRegion(jumpSpecAttack.getKeyFrame(animationTime, true));
            anim = jumpSpecAttack;
        }
        if (state == FALLING || state == GLIDING || state == REBOUNDING) {
            setRegion(fall.getKeyFrame(animationTime, true));
            anim = fall;
        }
        if (state == FALLATTACKING) {
            setRegion(fallAttack.getKeyFrame(animationTime, true));
            anim = fallAttack;
        }
        if (state == FALLSPECATTACKING) {
            setRegion(fallSpecAttack.getKeyFrame(animationTime, true));
            anim = fallSpecAttack;
        }
        if (state == CLIDLE) {
            setRegion(climbIdle.getKeyFrame(animationTime, true));
            anim = climbIdle;
        }
        if (state == CLIMBING) {
            setRegion(climbRun.getKeyFrame(animationTime, true));
            anim = climbRun;
        }
        if (state == CLATTACKING) {
            setRegion(climbAttack.getKeyFrame(animationTime, true));
            anim = climbAttack;
        }
        if (state == CLSPECATTACKING) {
            setRegion(climbSpecAttack.getKeyFrame(animationTime, true));
            anim = climbSpecAttack;
        }
        if (state == SWIMMING) {
            setRegion(swimRun.getKeyFrame(animationTime, true));
            anim = swimRun;
        }
        if (state == SWATTACKING) {
            setRegion(swimAttack.getKeyFrame(animationTime, true));
            anim = swimAttack;
        }
        if (state == SWSPECATTACKING) {
            setRegion(swimSpecAttack.getKeyFrame(animationTime, true));
            anim = swimSpecAttack;
        }
        if (state == CHARGING || state == FLYING) {
            setRegion(charge.getKeyFrame(animationTime, true));
            anim = charge;
        }
        if (state == ROLLING && stun) {
            setRegion(roll.getKeyFrame(animationTime, true));
            anim = roll;
        }
        if (state == ROLLING && !stun) {
            setRegion(rollingball);
            anim = ball;
        }
        if (state == UNROLLING && stun) {
            setRegion(unRoll.getKeyFrame(animationTime, true));
            anim = unRoll;
        }
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth() * 4 / 5, anim.getKeyFrame(animationTime, true).getRegionHeight() * 4 / 5);
    }

    //This detects whether the pixel knight has collided physically with an enemy and applies that enemy's effects.
    private void detectEnemies() {
        for (Creature creature : screen.creatures)
        {
            if (invulnerability) return;

            else if (creature.detectCollision() && !creature.dying) {
                loseHealth(1);
                creature.effects();
            }
        }
    }

    //Detects whether any part of the knight is within the bounds of a spiker, or a spiked block.
    private void detectSpikers() {
        for (float x = getX(); x < getX() + getWidth(); x ++)
            for (float y = getY(); y < getY() + getHeight(); y ++)
                if (isCellSpike(x, y))
                    loseHealth(1);

        for (Destructible destructible : screen.destructibles)
            if (destructible instanceof Spiker) {
                Spiker spiker = (Spiker) destructible;
                for (float i = getPosX(); i < getPosX() + getAttackingWidth(); i ++) {
                    for (float o = getY(); o < getY() + getHeight(); o ++) {
                        if (spiker.checkCollisionSpikes(i, o)) {
                            loseHealth(1);
                            return;
                        }
                    }
                }
            }
        for (Destructible destructible : screen.destructibles)
            if (destructible instanceof SpikeBlock) {
                SpikeBlock block = (SpikeBlock) destructible;
                for (float x = getX(); x < getX() + getWidth(); x ++) {
                    if (x > block.getX() && x < block.getX() + block.getWidth() && getY() + getHeight() > block.getY()
                            && getY() + getHeight() < block.getY() + block.getHeight() && block.getDirection() == 0)
                        loseHealth(1);
                    if (x > block.getX() && x < block.getX() + block.getWidth() && getY() < block.getY() + block.getHeight()
                            && getY() > block.getY() && block.getDirection() == 3)
                        loseHealth(1);
                }
                for (float y = getY(); y < getY() + getHeight(); y ++) {
                    if (y > block.getY() && y < block.getY() + block.getHeight() && getX() + getWidth() > block.getX()
                            && getX() + getWidth() < block.getX() + block.getWidth() && block.getDirection() == 2)
                        loseHealth(1);
                    if (y > block.getY() && y < block.getY() + block.getHeight() && getX() > block.getX()
                            && getX() < block.getX() + block.getWidth() && block.getDirection() == 2)
                        loseHealth(1);
                }
            }

    }

    //Detects whether the pixel knight has collided with any gems, and adds that gem's value to the player's amount.
    private void detectGems() {
        for (Gem gem : screen.gems)
        {
            if (gem.detectCollision() && gem.state != 1) {
                Array<Sound> sounds = new Array<Sound>();
                sounds.addAll(gemS1, gemS2, gemS3);
                playRandomSound(sounds);
                if (gem instanceof Emerald)
                    gems ++;
                if (gem instanceof Sapphire)
                    gems += 5;
                if (gem instanceof Ruby)
                    gems += 20;
                if (gem instanceof Diamond)
                    gems += 99;
                if (gems > 99) {
                    lives ++;
                    gems -= 99;
                }
                screen.gems.removeValue(gem, false);
                gem.setState(1);
            }
        }
    }

    //Detects whether any part of the pixel knight is inside a projectile.
    private void detectBalls() {
        for (Projectile projectile : screen.projectiles) {
            for (float x = getPosX(); x < getPosX() + getWidth(); x ++)
                for (float y = getY(); y < getY() + getHeight(); y ++) {
                    float distanceX = Math.abs(x - projectile.getX());
                    float distanceY = Math.abs(y - projectile.getY());
                    if (distanceX < projectile.getWidth() && distanceY < projectile.getHeight() && !invulnerability && projectile.enemy && projectile.lethal) {
                        loseHealth(1);
                        projectile.effects(this);
                    }
                }
        }
    }

    //If the pixelknight is above the platform and within the platform bounds, this method sets the position of the
    //knight atop the platform.
    private boolean detectPlatforms() {
        for (Platform platform : screen.platforms) {
            if (platform.detectCollision(this) && !transparent && !unlatchable && vel.y < 0) {
                setY(platform.getY() + platform.getHeight());
                vel.y = -1.1f;

                if (platform.vel.x != 0) {
                    vel.x = platform.vel.x;
                    pX = true;
                }
                else pX = false;
                if (state == REBOUNDING) {
                    setState(IDLE, true);
                    vel.x = 0;
                    vel.y = 0;
                    unStun();
                }
                if (state == FLYING) {
                    brake();
                    vel.y = 0;
                }
                if (state == CLIMBING || state == FALLING)
                    setState(IDLE, true);
                onPlatform = true;
                dJumpable = true;
                grounded = true;
                glidable = true;
                transparent = false;
                return true;
            }
        }
            if (onPlatform && state != CHARGING && state != FLYING) {
                vel.y = 0;
                vel.x = 0;
                onPlatform = false;
            }
        return false;
    }

    //If the pixel knight is attacking, this method sets the knight's width to be a bit lower than the actual attack frame,
    //so that the knight does not collide with objects or enemies when attacking with his sword.
    public float getAttackingWidth() {
        if (!attacking)
            return getWidth();
        else if (state == ATTACKING || state == SPECATTACKING || state == RUNATTACKING || state == RUNSPECATTACKING)
            return run1.getRegionWidth();
        else if (state == JUMPATTACKING || state == JUMPSPECATTACKING)
            return jump1.getRegionWidth();
        else if (state == FALLATTACKING || state == FALLSPECATTACKING)
            return fall1.getRegionWidth();
        else if (state == CLATTACKING || state == CLSPECATTACKING)
            return climbing1.getRegionWidth();
        return 0;
    }

    //These methods detect whether the pixelknight is INSIDE of a climbable or swimmable cell
    public boolean detectClimbing(float x, float y) {
        for (float step = 0; step < getWidth() - 5f; step += layer.getTileWidth() / 16)
            if (isCellClimbable(x + step, y)) {
                return true;
            }
            return false;
    }

    public boolean detectSwimming(float x, float y) {
        for (float step = 0; step < getWidth() - 5f; step += layer.getTileWidth() / 16)
            if (isCellSwimmable(x + step, y)) {
                if (!swimming && vel.y < 0)
                    vel.y = -0.8f;
                return true;
            }
        return false;
    }

    //This method determines whether the pixelknight can continue climbing in a certain direction.
    public void canClimb() {
        boolean detected = false;
        for (float step = 0; step < getWidth(); step += layer.getTileWidth() / 16)
            if (isCellClimbable(getPosX() + step, getY() + 10)) {
                climbable = true;
                detected = true;
            }
        if (!detected) {
            climbable = false;
            climbing = false;
        }
    }

    //Causes the pixelknight to rush forward and sets its state to charge.
    private void charge() {
        attackS1.play((float) storage.soundE / 100);
        cooldown = true;
        stun = true;
        dJumpable = false;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                cooldown = false;
            }
        }, 5);
        SVX(5);
        setState(CHARGING, true);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (state == CHARGING) {
                    brake();
                    vel.y = 0;
                }
            }
        }, 0.5f);
        timer.start();
    }

    //Slowly reduces the speed of the knight after charging for a time.
    private void brake() {
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if ((state == CHARGING || state == FLYING) && grounded) {
                    vel.x = vel.x / 2;
                }
            }
        }, 0.1f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if ((state == CHARGING || state == FLYING) && grounded) {
                    vel.x = vel.x / 2;
                }
            }
        }, 0.2f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if ((state == CHARGING || state == FLYING) && grounded) {
                    vel.x = vel.x / 2;
                }
            }
        }, 0.3f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if ((state == CHARGING || state == FLYING) && grounded) {
                    unStun();
                    setState(IDLE, true);
                }
            }
        }, 0.4f);
        timer.start();
    }

    //This method handles all functions associated with the knight's rolling ability.
    private void roll() {
        if (Gdx.input.isKeyPressed(storage.moveLeft) && vel.x > 0)
            vel.x -= 0.05f;
        if (Gdx.input.isKeyPressed(storage.moveRight) && vel.x < 0)
            vel.x += 0.05f;
        if (!Gdx.input.isKeyPressed(storage.moveLeft) && !Gdx.input.isKeyPressed(storage.moveRight)) {
            if (vel.x > 0)
                vel.x -= 0.01f;
            if (vel.x < 0)
                vel.x += 0.01f;
        }
        if (vel.x > 4)
            vel.x = 4;
        if (vel.x < -4)
            vel.x = -4;

        vel.x += acel.x;
        setOriginCenter();
        setRotation(getRotation() - 10 * dir);
        rMud.setDir(dir);
        if (vel.x == 0)
            setRotation(0);
        if (vel.x == 0 || !grounded || onSlopeLeft || onSlopeRight)
            screen.effects.removeValue(rMud, false);
        else if (!screen.effects.contains(rMud, false))
            screen.effects.add(rMud);

    }

    //Rebounds if the pixel knight hitss an object.
    private void rebound() {
        hurtS1.play((float) storage.soundE / 100);
        setState(REBOUNDING, true);
        setX(getPosX() - 5 * dir);
        vel.x = -dir;
        vel.y = 1.5f;
    }

    //This method activates if the pixel knight is hurt, by turning him red and white periodically.
    private void flickerSprite() {
        if (flickering) return;
        flickering = true;
        setColor(0, 0, 0, 0);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setColor(Color.RED);
            }
        }, 0.2f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setColor(0, 0, 0, 0);
            }
        }, 0.4f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setColor(Color.RED);
            }
        }, 0.6f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setColor(0, 0, 0, 0);
            }
        }, 0.8f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setColor(Color.WHITE);
                flickering = false;
            }
        }, 1);
        timer.start();


    }

    //Kills and removes the pixel knight instance from the game for a short time.
    public void death() {
        if (state == DEAD)
            return;
        death.play((float) storage.soundE / 100);
        depower();
        setState(DEAD, true);
        stun = true;
        vel.x = 0;
        vel.y = 0;
        lives --;
        if (lives == 0) {
            screen.gameOver();
            lives = 1;
        }
        setColor(Color.BLACK);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                screen.knightDeath();
                setColor(Color.WHITE);
                setState(IDLE, true);
                health = 10;
                screen.healthUI.update(health);
                unStun();
            }
        }, 1);
        timer.start();
    }

    //This method chooses the special power based on what the pixel knight has
    private void specDecider(float x, float y) {
        switch (power) {
            case NOTHING:
                specAttack();
                break;
            case MUD:
                powers.throwMud(x, y);
                throwMud.play((float) storage.soundE / 100);
                break;
            case CHARGE:
                if (!cooldown && (state == IDLE || state == RUNNING || state == JUMPING))
                    charge();
                break;
            case ROLL:
                if (swimming || stun || climbing)
                    break;
                rolling = !rolling;
                acel.x = 0;
                vel.x = 0;
                stun = true;
                if (rolling) {
                    rMud = new RollingMud(getPosX(), getY(), screen.atlases.get(8), layer, screen, this, dir);
                    setState(ROLLING, true);
                }
                else {
                    setRotation(0);
                    rMud.decaying();
                    screen.effects.removeValue(rMud, false);
                    setState(UNROLLING, true);
                    setOrigin(originX, originY);
                }
                Timer timer = new Timer();
                timer.scheduleTask(new Timer.Task() {
                    @Override
                    public void run() {
                        unStun();
                        if (!screen.effects.contains(rMud, false) && rolling)
                            screen.effects.add(rMud);
                        if (!rolling)
                            setState(IDLE, true);
                    }
                }, 1);
                timer.start();
                break;
        }
    }

    //These methods hurt enemies within a certain distance of the pixel knight depending on his direction.
    private void attack() {
        if (attacking || stun)
            return;
        attacking = true;

        Timer attackTimer = new Timer();
        attackTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (attacking) {
                    if (attackDir == LEFT)
                        setX(getX() + 7);
                    attacking = false;
                    attackDir = dir;
                    update(Gdx.graphics.getDeltaTime());
                }
            }
        }, 0.5f);
        attackTimer.start();

        if (state == CLIDLE || state == CLIMBING)
            setState(CLATTACKING, true);
        else if (state == SWIMMING)
            setState(SWATTACKING, true);
        else if (state == RUNNING)
            setState(RUNATTACKING, true);
        else if (state == JUMPING)
            setState(JUMPATTACKING, true);
        else if (state == FALLING)
            setState(FALLATTACKING, true);
        else setState(ATTACKING, true);

        swAttack();
    }

    private void specAttack() {
        if (attacking)
            return;
        attacking = true;

        Timer attackTimer = new Timer();
        attackTimer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (attacking) {
                    if (attackDir == LEFT)
                        setX(getX() + 7);
                    attacking = false;
                    attackDir = dir;
                    update(Gdx.graphics.getDeltaTime());
                }
            }
        }, 0.5f);
        attackTimer.start();

        if (state == RUNNING)
            setState(RUNSPECATTACKING, true);
        else if (state == JUMPING)
            setState(JUMPSPECATTACKING, true);
        else if (state == FALLING)
            setState(FALLSPECATTACKING, true);
        else if (state == CLIDLE || state == CLIMBING)
            setState(CLSPECATTACKING, true);
        else if (state == SWIMMING)
            setState(SWSPECATTACKING, true);
        else setState(SPECATTACKING, true);

        specSwAttack();
    }

    private void swAttack() {
        Array<Sound> sounds = new Array<Sound>();
        sounds.addAll(attackS1, attackS2, attackS3);
        playRandomSound(sounds);

        if (attackDir == LEFT)
            setX(getX() - 7);

        for (final Creature creature : screen.creatures)
        {
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    if (creature.isHit(attackDir, 8) && creature.vulnerable(vel) && attacking)
                        creature.damage(dmg);
                }
            }, 0.405f);
            timer.start();
        }
        for (final Destructible destructible : screen.destructibles)
        {
            final PixelKnight pk = this;
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    if (destructible.isHit(attackDir, 8) && attacking)
                        destructible.explode(pk);
                }
            }, 0.405f);
            timer.start();
        }
    }

    private void specSwAttack() {
        Array<Sound> sounds = new Array<Sound>();
        sounds.addAll(attackS1, attackS2, attackS3);
        playRandomSound(sounds);

        if (attackDir == LEFT)
            setX(getX() - 7);

        for (final Creature creature : screen.creatures)
        {
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    if (creature.isHit(attackDir, 8) && creature.vulnerable(vel) && attacking)
                        creature.damage(dmg);
                    if (creature.health == 0) {
                        absorb(creature);
                    }
                }
            }, 0.405f);
        }
        for (final Destructible destructible : screen.destructibles)
        {
            final PixelKnight pk = this;
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    if (destructible.isHit(attackDir, 8) && attacking)
                        destructible.explode(pk);
                }
            }, 0.405f);
            timer.start();
        }
    }

    //Subtracts health from the pixel knight.
    public void loseHealth(int h) {
        if (!invulnerability) {
            Array<Sound> sounds = new Array<Sound>();
            sounds.addAll(hurtS1, hurtS2, hurtS3);
            playRandomSound(sounds);

            health -= (h - armor);
            screen.healthUI.update(health);
            flickerSprite();
            invulnerability = true;
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    invulnerability = false;
                }
            }, 1.2f);
            timer.start();
            if (state == GLIDING) {
                glidable = false;
                setState(FALLING, true);
            }
            if (health == 0)
                death();
        }
    }

    //This creates a new power for the pixel knight based on the creature killed.
    private void absorb(Creature creature) {
        if (creature instanceof MudSlinger)
            power = MUD;
        if (creature instanceof Charger)
            power = CHARGE;
        if (creature instanceof Bat)
            power = GLIDE;
        if (creature instanceof Jumper)
            power = HJUMP;
        if (creature instanceof Roller)
            power = ROLL;
        if (power != NOTHING) {
            Array<Sound> sounds = new Array<Sound>();
            sounds.addAll(pwrUpS1, pwrUpS2, pwrUpS3);
            playRandomSound(sounds);
            setColor(Color.CYAN);
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    setColor(Color.WHITE);
                }
            }, 0.8f);
            timer.start();
        }
        screen.powerUI.update(power);
    }

    //Modifies the velocity of the knight for a short time.
    public void modifyVelocity(float x, float y, float time) {
        vel.x = x;
        vel.y = y;
        stun = true;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                unStun();
            }
        }, time);
        timer.start();
    }

    public void setModifier(float modX, float modY, float time) {
        modifierX = modX;
        modifierY = modY;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                resetModifier();
            }
        }, time);
    }

    //Sets the velocity of the pixelknight depending on the modifier.
    private void SVX(float x) {
        vel.x = x * dir - modifierX * dir;
        if (vel.x < 0 && dir == RIGHT)
            vel.x = 0;
        else if (vel.x > 0 && dir == LEFT)
            vel.x = 0;
    }

    private void SVY(float y) {
        vel.y = y - modifierY;
        if (vel.y < 0 && y > 0)
            vel.y = 0;
        if (vel.y > 0 && y < 0)
            vel.y = 0;
    }

    private void resetModifier() {
        modifierX = 0;
        modifierY = 0;
    }

    private void unStun() {
        stun = false;
    }

    //Adds and receives the slope.
    public void addSlope(boolean right, RectangleMapObject rMap)
    {
        if (right)
        rSlopes.add(rMap);
        else lSlopes.add(rMap);
    }

    public Array<RectangleMapObject> getSlope(boolean right)
    {
        if (right)
            return rSlopes;
        return lSlopes;
    }

    //This periodically handles swimming, rolling, and charging states.
    private void handleEvents() {
        swimming = detectSwimming(getPosX(), getY());
        if (swimming && rolling) {
            rolling = false;
            vel.x = 0;
            acel.x = 0;
            setRotation(0);
            rMud.decaying();
            screen.effects.removeValue(rMud, false);
        }

        if (!climbing && !swimming && state != GLIDING)
            vel.y -= GRAVITY;
        if (vel.y < -2.5f) vel.y = -2.5f;

        if (rolling && !stun)
            roll();

        if (state == CHARGING && !grounded) {
            setState(FLYING, true);
        }

        if (!detectPlatforms() && !collidesDestructibleBottom())
            pX = false;
    }

    private void updateTime(float deltaTime) {
        animationTime += deltaTime;
    }

    //Removes the rolling power of the knight to prevent issues arising from the rolling frames and characteristics.
    private void depower() {
        if (rolling) {
            rolling = false;
            setRotation(0);
            vel.x = 0;
            acel.x = 0;
            if (rMud != null) {
                rMud.decaying();
                screen.effects.removeValue(rMud, false);
            }
            stun = true;
            setState(UNROLLING, true);
            setOrigin(originX, originY);
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    unStun();
                    setState(IDLE, true);
                }
            }, 1);
            timer.start();
        }
    }

    //If the pixel knight is attacking, it's scale becomes reversed to preserve its width. This causes collisions
    //to become displaced, which is fixed by this method.
    public float getPosX() {
        if (attackDir == LEFT && attacking)
            return getX() + 7;
        else
            return getX();
    }

    //Plays a random sound based on the array given.
    private void playRandomSound(Array<Sound> sounds) {
        int random = (int) (Math.random() * sounds.size);
        sounds.get(random).play((float) storage.soundE / 100);
    }

    public boolean isRolling() {
        return rolling;
    }

    public int getDirection() {
        return dir;
    }

    public int getState() {
        return state;
    }
}
