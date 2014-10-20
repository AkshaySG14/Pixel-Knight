package com.inoculates.knight.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.projectiles.Projectile;
import com.inoculates.knight.screens.GameScreen;

public class Charger extends Creature {
    static final int MOVING = 4;
    static final int CHARGING = 5;
    static final int READYING = 6;
    static final int REBOUNDING = 7;

    boolean cooldown = false;

    Animation idle;
    Animation run;

    TextureAtlas.AtlasRegion idle1 = atlas.findRegion("idle1");
    TextureAtlas.AtlasRegion run1 = atlas.findRegion("run1");
    TextureAtlas.AtlasRegion run2 = atlas.findRegion("run2");
    TextureAtlas.AtlasRegion run3 = atlas.findRegion("run3");
    TextureAtlas.AtlasRegion run4 = atlas.findRegion("run4");
    TextureAtlas.AtlasRegion run5 = atlas.findRegion("run5");
    TextureAtlas.AtlasRegion run6 = atlas.findRegion("run6");
    TextureAtlas.AtlasRegion run7 = atlas.findRegion("run7");
    TextureAtlas.AtlasRegion run8 = atlas.findRegion("run8");

    private Sound chargeS = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Attack3.wav"));
    private Sound reboundS = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Hit_Hurt3.wav"));

    public Charger(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, float sX, float sY) {
        super(screen, atlas, layer, map, 2, 0, true, false, sX, sY);
        state = IDLE;
        setX(sX);
        setY(sY);
        createAnimations();
        setSize(19, 30);
        setRegion(idle.getKeyFrame(animationTime, true));
    }

    public void draw(Batch batch) {
        update();
        super.draw(batch);
    }

    //Moves the charger after three seconds for three seconds, and then makes it stand still for three seconds.
    private void update() {
        if (vel.x == 0 && vel.y == 0 && state != READYING)
            setState(IDLE);
        if (time > 3 && time < 3.05f && state != CHARGING && state != REBOUNDING && state != READYING) {
            movement();
            setState(MOVING);
        }
        if (time > 6 && state != CHARGING && state != REBOUNDING && state != READYING) {
            setState(IDLE);
            vel.x = 0;
            time = 0;
        }
    }

    protected void tryMove() {
        collidesSlopeLeft();
        collidesSlopeRight();

        if (state != CHARGING && state != READYING && state != REBOUNDING && !cooldown )
            checkCharge();

        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;
        setX(getX() + vel.x);
        if (vel.x < 0) {
            collisionX = collidesLeft();
            if (collidesLeft())
                dir = RIGHT;
        } else if (vel.x > 0) {
            collisionX = collidesRight();
            if (collidesRight()) {
                dir = LEFT;
            }
        }

        if (collisionX && !onSlopeRight && !onSlopeLeft) {
            if (state == CHARGING)
            rebound();
            else vel.x = 0;
            setX(oldX);
        }

        setY(getY() + vel.y);
        if (vel.y < 0) {
            collisionY = collidesBottom();
        } else if (vel.y > 0)
            collisionY = collidesTop();

        if (collisionY) {
            if (state == REBOUNDING) {
                setState(IDLE);
                vel.x = 0;
                vel.y = 0;
                time = 0;
            }
            setY(oldY);
            vel.y = 0;
        }
    }

    //Sets the velocity of the charger depending on its direction.
    private void movement() {
        if (state == CHARGING || state == READYING || state == REBOUNDING) return;
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
            SVX(0.2f);
        if (dir == LEFT)
            SVX(-0.2f);
    }

    protected void chooseSprite() {
        if (state == IDLE)
            setRegion(idle.getKeyFrame(animationTime, true));
        if (state == MOVING)
            setRegion(run.getKeyFrame(animationTime, true));
        if (state == CHARGING)
            setRegion(run.getKeyFrame(animationTime, true));
        if (state == READYING)
            setRegion(idle.getKeyFrame(animationTime, true));
    }

    private void createAnimations() {
        idle = new Animation(0.2f, idle1);
        run = new Animation(0.125f, run1, run2, run3, run4, run5, run6, run7, run8);
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

    //Checks if the knight is within his sight of vision.
    private void checkCharge() {
        final float dX, dY;
        dX = Math.abs(getX() - screen.pKnight.getX());
        dY = Math.abs(getY() - screen.pKnight.getY());
        boolean fCorr = (screen.pKnight.getX() - getX() > 0 && dir == RIGHT) || (screen.pKnight.getX() - getX() < 0 && dir == LEFT);
        if (dX < screen.camera.viewportWidth / 2 && (dY < 1 || (onSlopeLeft || onSlopeRight || screen.pKnight.onSlopeLeft || screen.pKnight.onSlopeRight)) && fCorr) {
            cooldown = true;
            setState(READYING);
            vel.x = 0;
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    charge();
                }
            }, 0.8f);
            timer.start();
        }
    }

    //Makes the charger charge by increasing its x velocity.
    private void charge() {
        chargeS.play((float) screen.storage.soundE / 100);
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                cooldown = false;
            }
        }, 5);
        timer.start();
        SVX(5 * dir);
        setState(CHARGING);
        Timer timer2 = new Timer();
        timer2.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if (state != IDLE && state != REBOUNDING)
                    brake();
                time = 0;
            }
        }, 0.75f);
        timer2.start();
    }

    private void brake() {
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if ((state == CHARGING && !collidesBottom())) {
                    vel.x = vel.x / 2;
                }
            }
        }, 0.1f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if ((state == CHARGING && !collidesBottom())) {
                    vel.x = vel.x / 2;
                }
            }
        }, 0.2f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if ((state == CHARGING && !collidesBottom())) {
                    vel.x = vel.x / 2;
                }
            }
        }, 0.3f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                if ((state == CHARGING && !collidesBottom())) {
                    vel.x = 0;
                    setState(IDLE);
                }
            }
        }, 0.4f);
        timer.start();
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
        if (state == CHARGING)
        screen.pKnight.modifyVelocity(2 * dir, 3, 1);
    }

    protected void flipSprite() {
        if (dir == LEFT)
            setFlip(false, false);
        else
            setFlip(true, false);
    }

    //The charger is only vulnerable if struck from behind.
    public boolean vulnerable(Object object) {
        if (object instanceof Vector2) {
            if (dir == pK.getDirection())
                return true;
        }
        if (object instanceof Projectile) {
            Projectile projectile = (Projectile) object;
            if ((dir == RIGHT && projectile.vel.x > 0) || (dir == LEFT && projectile.vel.x < 0))
            return true;
        }
        return false;
    }

    public void rebound() {
        reboundS.play((float) screen.storage.soundE / 100);
        setState(REBOUNDING);
        vel.x = dir;
        vel.y = 1.5f;
    }

    protected boolean harmful() {
        return true;
    }

}
