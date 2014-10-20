package com.inoculates.knight.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.effects.RollingMud;
import com.inoculates.knight.screens.GameScreen;

public class Roller extends Creature {
    static final int RUN = 4;

    boolean rolling = false;

    Animation idle;
    RollingMud mud;

    TextureAtlas.AtlasRegion idle1 = atlas.findRegion("idle1");
    TextureAtlas.AtlasRegion idle2 = atlas.findRegion("idle2");
    Vector2 acel = new Vector2(0, 0);
    float acceleration = 0;

    public Roller(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, float sX, float sY) {
        super(screen, atlas, layer, map, 1, 0, true, false, sX, sY);
        state = SPAWN;
        setX(sX);
        setY(sY);
        createAnimations();
        setSize(16, 16);
        setRegion(idle.getKeyFrame(animationTime, true));
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    protected void tryMove()
    {
        checkAttack();
        collidesSlopeLeft();
        collidesSlopeRight();
        dir = (int) (Math.signum(pK.getX() - getX()));

        if (mud != null)
            mud.setDir(dir);

        if (!rolling) {
            setRotation(0);
            vel.x = 0;
            acel.x = 0;
        }

        else {
            setOriginCenter();
            setRotation(getRotation() - 10 * dir);
            if (dir == LEFT && vel.x > 0)
                acceleration = -0.075f;
            else if (dir == RIGHT && vel.x < 0)
                acceleration = 0.075f;
            else acceleration = 0.05f * dir;

            acel.x = acceleration;
            vel.x += acel.x;

            if (vel.x > 4)
                vel.x = 4;
            if (vel.x < -4)
                vel.x = -4;
        }

        if (vel.x == 0)
            setState(IDLE);
        else setState(RUN);

        float oldX = getX(), oldY = getY();
        boolean collisionX = false, collisionY = false;
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
            collisionY = collidesBottom();
        } else if (vel.y > 0)
            collisionY = collidesTop();

        if (collisionY) {
            setY(oldY);
            vel.y = 0;
        }
    }

    //Rolls towards the knight when within a certain distance.
    private void checkAttack() {
        float dX = Math.abs(getX() - screen.pKnight.getX());
        float dY = Math.abs(getY() - screen.pKnight.getY());
        if (dX < screen.camera.viewportWidth / 4 && dY < screen.camera.viewportHeight / 4) {
            if (!rolling) {
                mud = new RollingMud(getX(), getY(), atlas, layer, screen, this, dir);
                screen.effects.add(mud);
                rolling = true;
            }
        }
        else if (rolling) {
            rolling = false;
            if (mud != null) {
                mud.decaying();
                screen.effects.removeValue(mud, false);
            }
        }
    }

    protected void chooseSprite()
        {
            setRegion(idle.getKeyFrame(animationTime, true));
        }

    private void createAnimations() {
        idle = new Animation(0.25f, idle1, idle1, idle1, idle1, idle1, idle1, idle1, idle2);
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
        }, 0.5f);
        timer.start();
    }

    public void death() {
        setState(DEAD);
        if (mud != null) {
            mud.decaying();
            screen.effects.removeValue(mud, false);
        }
        screen.creatures.removeValue(this, true);
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
