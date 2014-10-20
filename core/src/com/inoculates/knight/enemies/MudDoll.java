package com.inoculates.knight.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.screens.GameScreen;

public class MudDoll extends Creature {
    static final int RUN = 4;

    boolean moving = true;
    boolean stoppable = true;

    Animation idle;
    Animation run;
    Rectangle leftBounds, rightBounds;

    TextureAtlas.AtlasRegion run1 = atlas.findRegion("Run1");
    TextureAtlas.AtlasRegion run2 = atlas.findRegion("Run2");
    TextureAtlas.AtlasRegion run3 = atlas.findRegion("Run3");
    TextureAtlas.AtlasRegion run4 = atlas.findRegion("Run4");

    TextureAtlas.AtlasRegion idle1 = atlas.findRegion("Idle1");
    TextureAtlas.AtlasRegion idle2 = atlas.findRegion("Idle2");
    TextureAtlas.AtlasRegion idle3 = atlas.findRegion("Idle3");
    TextureAtlas.AtlasRegion idle4 = atlas.findRegion("Idle4");

    public MudDoll (GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, float sX, float sY,
                    Rectangle l, Rectangle r) {
        super(screen, atlas, layer, map, 1, 0, true, false, sX, sY);
        state = SPAWN;
        setX(sX);
        setY(sY);
        createAnimations();
        setSize(20, 20);
        leftBounds = l;
        rightBounds = r;
        setRegion(idle.getKeyFrame(animationTime, true));
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }


    //Moves left and right, stopping at the middle of its bounds.
    protected void tryMove()
    {
        collidesSlopeLeft();
        collidesSlopeRight();

        if (getX() + getWidth() > rightBounds.getX())
            dir = LEFT;
        else if (getX() < leftBounds.getX())
            dir = RIGHT;
        else if (Math.abs(getX() - spawnX) < 1 && stoppable) {
            moving = false;
            stoppable = false;
            setState(IDLE);
            vel.x = 0;
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                        moving = true;
                }
            }, 1.5f);
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    stoppable = true;
                }
            }, 4);
            timer.start();
        }

        if (!moving)
            vel.x = 0;
        else {
            if (dir == RIGHT)
                SVX(0.5f);
            if (dir == LEFT)
                SVX(-0.5f);
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

    protected void chooseSprite()
        {
            if (state == IDLE)
                setRegion(idle.getKeyFrame(animationTime, true));
            if (state == RUN)
                setRegion(run.getKeyFrame(animationTime, true));
        }

    private void createAnimations() {
        idle = new Animation(0.25f, idle1, idle2, idle3, idle4);
        run = new Animation(0.25f, run1, run2, run3, run4);
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

    public void effects() {

    }

    protected void flipSprite() {
        if (dir == LEFT)
            setFlip(false, false);
        else
            setFlip(true, false);
    }

    protected boolean harmful() {
        return true;
    }

    public boolean vulnerable(Object object) {
        return true;
    }
}
