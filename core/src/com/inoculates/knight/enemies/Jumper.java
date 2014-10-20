package com.inoculates.knight.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.screens.GameScreen;

public class Jumper extends Creature {
    static final int MOVING = 4;
    static final int READYING = 5;
    static final int JUMPING = 6;
    static final int FALLING = 7;

    boolean hostile = false;
    boolean grounded = true;

    Animation idle;
    Animation run;
    Animation ready;
    Animation jump;

    TextureAtlas.AtlasRegion idle1 = atlas.findRegion("idle1");
    TextureAtlas.AtlasRegion idle2 = atlas.findRegion("idle2");
    TextureAtlas.AtlasRegion jump1 = atlas.findRegion("flying1");
    TextureAtlas.AtlasRegion jump2 = atlas.findRegion("flying2");
    TextureAtlas.AtlasRegion run1 = atlas.findRegion("run1");
    TextureAtlas.AtlasRegion run2 = atlas.findRegion("run2");
    TextureAtlas.AtlasRegion run3 = atlas.findRegion("run3");
    TextureAtlas.AtlasRegion run4 = atlas.findRegion("run4");
    TextureAtlas.AtlasRegion ready1 = atlas.findRegion("readying1");
    TextureAtlas.AtlasRegion ready2 = atlas.findRegion("readying2");
    TextureAtlas.AtlasRegion ready3 = atlas.findRegion("readying3");
    TextureAtlas.AtlasRegion ready4 = atlas.findRegion("readying4");

    private Sound jumpS1 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Jump.wav")),
            jumpS2 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Jump2.wav")),
            jumpS3 = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Jump3.wav"));

    public Jumper(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, float sX, float sY) {
        super(screen, atlas, layer, map, 2, 0, true, false, sX, sY);
        state = IDLE;
        setX(sX);
        setY(sY);
        createAnimations();
        setSize(20, 20);
        setRegion(idle.getKeyFrame(animationTime, true));
    }

    public void draw(Batch batch) {
        super.draw(batch);
        update();
    }

    //Checks whether the pixel knight is within attacking distance.
    private void update() {
        checkHostile();
        final float dX, dY;
        dX = Math.abs(getX() - screen.pKnight.getX());
        dY = Math.abs(getY() - screen.pKnight.getY());
        if (!hostile) {
            if (time > 2 && time < 2.01f && state != JUMPING && state != FALLING && state != READYING && dX < screen.camera.viewportWidth / 2 && dY < screen.camera.viewportHeight / 2) {
                movement();
                setState(MOVING);
            }
            if (time > 4 && state != JUMPING && state != FALLING && state != READYING) {
                setState(IDLE);
                vel.x = 0;
                time = 0;
            }
        }
        else if (state != READYING && state != JUMPING && state != FALLING && grounded && vel.y <= 0) {
            jump();
        }

        if (grounded && state == FALLING) {
            setState(IDLE);
            vel.x = 0;
        }
    }

    protected void tryMove()
        {
            collidesSlopeLeft();
            collidesSlopeRight();

            float oldX = getX(), oldY = getY();
            boolean collisionX = false, collisionY = false, collisionP;
            setX(getX() + vel.x);
            if (vel.x < 0) {
                collisionX = collidesLeft();
                if (collidesLeft())
                    dir = RIGHT;
            }
            else if (vel.x > 0) {
                collisionX = collidesRight();
                if (collidesRight())
                    dir = LEFT;
            }

            if (collisionX) {
                setX(oldX);
                vel.x = 0;
            }

            setY(getY() + vel.y);
            if (vel.y < 0) {
                if (state == JUMPING)
                setState(FALLING);
                collisionY = collidesBottom();
                collisionP = collidesPlatform();
                grounded = !(!collisionY && !collisionP && !onPlatform && !collidesSlopeRight() && !collidesSlopeLeft());
            }
            else if (vel.y > 0) {
                setState(JUMPING);
                collisionY = collidesTop();
            }

            if (collisionY) {
                setY(oldY);
                vel.y = 0;
            }
        }

    //Moves only if the jumper is not doing anything else.
    private void movement() {
        if (state == JUMPING || state == READYING || state == FALLING) return;
        int random = (int) (Math.random() * 2);
        if (random == 0)
            dir = LEFT;
        if (random == 1)
            dir = RIGHT;

        checkCollision();

        if (detectCliffRight())
            dir = LEFT;
        if (detectCliffLeft())
            dir = RIGHT;

        if (dir == RIGHT)
            SVX(1);
        if (dir == LEFT)
            SVX(-1);

        if (vel.x == 0 && vel.y == 0)
            setState(IDLE);
        else setState(MOVING);
    }

    protected void chooseSprite() {
        Animation anim = idle;
        if (state == IDLE) {
            setRegion(idle.getKeyFrame(animationTime, true));
            anim = idle;
        }
        if (state == MOVING) {
            setRegion(run.getKeyFrame(animationTime, true));
            anim = run;
        }
        if (state == READYING) {
            setRegion(ready.getKeyFrame(animationTime, true));
            anim = ready;
        }
        if (state == JUMPING || state == FALLING) {
            setRegion(jump.getKeyFrame(animationTime, true));
            anim = jump;
        }
        setSize(anim.getKeyFrame(animationTime, true).getRegionWidth(), anim.getKeyFrame(animationTime, true).getRegionHeight());
    }

    private void createAnimations() {
        run = new Animation(0.25f, run1, run2, run3, run4);
        ready = new Animation(0.1875f, ready1, ready2, ready3, ready4);
        idle = new Animation(0.5f, idle1, idle2);
        jump = new Animation(0.5f, jump1, jump2);
    }

    private void checkCollision() {
        for (float i = getX(); i < getX() + 20; i += layer.getTileHeight()) {
            if (isCellBlocked(i, getY()))
                dir = LEFT;
        }
        for (float i = getX(); i > getX() - 20; i -= layer.getTileHeight()) {
            if (isCellBlocked(i, getY()))
                dir = RIGHT;
        }
    }

    private void checkHostile() {
        final float dX, dY;
        dX = Math.abs(getX() - screen.pKnight.getX());
        dY = Math.abs(getY() - screen.pKnight.getY());
        hostile = (dX < screen.camera.viewportWidth / 4 && dY < screen.camera.viewportHeight / 4);
    }

    private void jump() {
        vel.x = 0;
        setState(READYING);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (state == JUMPING || state == FALLING)
                    return;
                targetJump();
            }
        }, 0.75f);
        timer.start();
    }

    //The jumper jumps towards the pixel knight depending on the angle between it and him.
    private void targetJump() {
        Array<Sound> sounds = new Array<Sound>();
        sounds.addAll(jumpS1, jumpS2, jumpS3);
        playRandomSound(sounds);
        float angle = (float) Math.atan2(screen.pKnight.getY() - getY(), screen.pKnight.getX() - getX());
        float oX = (float) Math.cos(angle);
        float dX = Math.abs(screen.pKnight.getX() - getX());
        float dY = screen.pKnight.getY() - getY();
        if (dY < 0) dY = 1.5f;
        float fX = (float) (dX / 2 + Math.random() * 5) / 30;
        float fY = (float) (dY / 2 + Math.random() * 5) / 25 + 3f;
        if (fY > 5) fY = 5;
        if (fX > 5) fX = 5;
        dir = (int) (1 * Math.signum(oX));
        SVX(oX * fX);
        SVY(fY);
    }

    protected boolean priorities(int cState) {
        return true;
    }

    protected void dying() {
        setState(DYING);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                death();
            }
        }, 0.2f);
        timer.start();
    }

    public void effects() {

    }

    protected void flipSprite() {

    }

    protected boolean harmful() {
        return true;
    }

    public boolean vulnerable(Object object) {
        return true;
    }
}
