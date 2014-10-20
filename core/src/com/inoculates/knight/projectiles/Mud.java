package com.inoculates.knight.projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.enemies.Creature;
import com.inoculates.knight.pixelknight.PixelKnight;
import com.inoculates.knight.screens.GameScreen;

public class Mud extends Projectile {
    float forceX;
    float forceY;
    float offsetX;
    float offsetY;

    Animation launch;
    Animation fly;
    Animation explode;

    TextureAtlas.AtlasRegion launching = atlas.findRegion("launching");
    TextureAtlas.AtlasRegion flying = atlas.findRegion("flying");
    TextureAtlas.AtlasRegion exploding1 = atlas.findRegion("exploding1");
    TextureAtlas.AtlasRegion exploding2 = atlas.findRegion("exploding2");
    TextureAtlas.AtlasRegion exploding3 = atlas.findRegion("exploding3");

    private Sound explodeS = Gdx.audio.newSound(Gdx.files.internal("data/sounds/Explosion5.wav"));

    public Mud(float sX, float sY, float oX, float oY, float fX, float fY, TextureAtlas atlas,
               TiledMapTileLayer layer, GameScreen screen, boolean enemy) {
        super(layer, atlas, screen, sX, sY, 0);
        createAnimations();
        setX(sX);
        setY(sY);
        offsetX = oX;
        offsetY = oY;
        forceX = fX;
        forceY = fY;
        lethal = true;
        launch();
        this.enemy = enemy;
        setState(LAUNCHING);
        setRegion(launch.getKeyFrame(animationTime, true));
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                setState(FLYING);
            }
        }, 0.4f);
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                decaying();
            }
        }, 5);
        timer.start();
        setSize(10, 8);
    }

    public void draw(Batch batch) {
        super.draw(batch);
        update();
    }

    //The mud is constantly affected by gravity and will arc downwards.
    protected void update()
    {
        if (state != DECAYING && !swimming) {
            vel.y -= GRAVITY;
            if (vel.y < -3.5f)
                vel.y = -3.5f;
        }
        else if (swimming && state != DECAYING) {
            vel.y -= GRAVITY / 10;
            if (vel.y < -1.6f)
                vel.y = -1.6f;
        }
    }

    protected void tryMove()
    {
        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;

        setX(getX() + vel.x);

        if (vel.x < 0)
            collisionX = collidesLeft();

        else if (vel.x > 0)
            collisionX = collidesRight();

        if (collisionX) {
            setX(oldX);
            vel.x = 0;
            if (state != DECAYING) decaying();
        }

        setY(getY() + vel.y);

        if (vel.y < 0) {
            collisionY = collidesBottom();
        } else if (vel.y > 0)
            collisionY = collidesTop();

        if (collisionY || collidesSlopeRight() || collidesSlopeLeft() || collidesPlatform() || detectPlatforms()) {
            setY(oldY);
            vel.y = 0;
            if (state != DECAYING) decaying();
        }
    }

    //Sets the velocities to the direction multiplied by the force.
    public void launch() {
        vel.x = offsetX * forceX;
        vel.y = Math.signum(offsetY) * forceY;
    }

    protected void chooseSprite()
        {
            flipSprite();

            if (state == LAUNCHING)
                setRegion(launch.getKeyFrame(animationTime, true));

            if (state == FLYING)
                setRegion(fly.getKeyFrame(animationTime, true));

            if (state == DECAYING) {
                setRegion(explode.getKeyFrame(animationTime, false));
            }
        }

    private void createAnimations() {
        launch = new Animation(0.2f, launching);
        fly = new Animation(0.2f, flying);
        explode = new Animation(0.1f, exploding1, exploding2, exploding3);
    }

    //Creates the exploding animation and the sound effect.
    protected void decaying() {
        if (screen.projectiles.contains(this, false))
            explodeS.play((float) screen.storage.soundE / 100);
        animationTime = 0;
        setState(DECAYING);
        lethal = false;
        vel.x = 0;
        vel.y = 0;
        Timer timer = new Timer();
        timer.scheduleTask(new Timer.Task() {
            @Override
            public void run() {
                destroy();
            }
        }, 0.35f);
        timer.start();
    }

    protected void destroy() {
        setState(DESTROYED);
    }

    public void effects(Object object) {
        decaying();
        if (object instanceof PixelKnight)
        screen.pKnight.setModifier(0.5f, 0.5f, 1.5f);
        if (object instanceof Creature) {
            Creature creature = (Creature) object;
            creature.damage(1);
        }
    }

    private void flipSprite() {
        if (vel.x < 0) {
            if (launching.isFlipX())
                launching.flip(true, false);
            if (flying.isFlipX())
                flying.flip(true, false);
            if (exploding1.isFlipX())
                exploding1.flip(true, false);
            if (exploding2.isFlipX())
                exploding2.flip(true, false);
            if (exploding3.isFlipX())
                exploding3.flip(true, false);
        }
        if (vel.x > 0) {
            if (!launching.isFlipX())
                launching.flip(true, false);
            if (!flying.isFlipX())
                flying.flip(true, false);
            if (!exploding1.isFlipX())
                exploding1.flip(true, false);
            if (!exploding2.isFlipX())
                exploding2.flip(true, false);
            if (!exploding3.isFlipX())
                exploding3.flip(true, false);
        }
    }

    protected boolean priorities(int cState) {
        return !((state == DECAYING || state == DESTROYED) && cState != DESTROYED);
    }

}
