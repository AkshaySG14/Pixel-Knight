package com.inoculates.knight.enemies;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.screens.GameScreen;

public class Jellyfish extends Creature {
    static final int ASCENDING = 4;
    static final int DESCENDING = 5;

    boolean moving = true;
    Rectangle upperBounds, lowerBounds;

    Animation idle;
    Animation runD;
    Animation runU;
    TextureAtlas.AtlasRegion idle1 = atlas.findRegion("idle1");
    TextureAtlas.AtlasRegion idle2 = atlas.findRegion("idle2");
    TextureAtlas.AtlasRegion idle3 = atlas.findRegion("idle3");
    TextureAtlas.AtlasRegion idle4 = atlas.findRegion("idle4");

    TextureAtlas.AtlasRegion run1 = atlas.findRegion("run1");
    TextureAtlas.AtlasRegion run2 = atlas.findRegion("run2");
    TextureAtlas.AtlasRegion run3 = atlas.findRegion("run3");
    TextureAtlas.AtlasRegion run4 = atlas.findRegion("run4");

    public Jellyfish(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, float sX, float sY,
                     Rectangle u, Rectangle d) {
        super(screen, atlas, layer, map, 1, 0, false, true, sX, sY);
        setX(sX);
        setY(sY);
        createAnimations();
        setSize(18, 18);
        setRegion(run1);
        setState(IDLE);
        dir = STILL;
        upperBounds = u;
        lowerBounds = d;
        SVY(0.5f);
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    //Moves the jellyfish up and down depending on how far it is. There is a random change it will stand still however.
    protected void tryMove()
    {
        int random = (int) (Math.random() * 1000);
        if (random == 1 && moving) {
            final float velocity = vel.y;
            final int cState = state;
            moving = false;
            setState(IDLE);
            vel.y = 0;
            Timer timer = new Timer();
            timer.scheduleTask(new Timer.Task() {
                @Override
                public void run() {
                    moving = true;
                    vel.y = velocity;
                    setState(cState);
                }
            }, 3);
            timer.start();
        }
        if (!moving) return;

        if (getY() + getHeight() > upperBounds.getY()) {
            SVY(-0.5f);
            setState(DESCENDING);
        }
        else if (getY() < lowerBounds.getY()) {
            SVY(0.5f);
            setState(ASCENDING);
        }

        setY(getY() + vel.y);
    }

    protected void chooseSprite()
        {
            if (state == IDLE)
                setRegion(idle.getKeyFrame(animationTime, true));
            if (state == DESCENDING)
                setRegion(runD.getKeyFrame(animationTime, true));
            if (state == ASCENDING)
                setRegion(runU.getKeyFrame(animationTime, true));
        }

    private void createAnimations() {
        idle = new Animation(0.1666f, idle1, idle2, idle3, idle4, idle3, idle2);
        runU = new Animation(0.2f, run1, run2, run3, run4, run3);
        runD = new Animation(0.2f, run4, run3, run1, run2, run3);
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

    }

    protected boolean harmful() {
        return true;
    }

    public boolean vulnerable(Object object) {
        return true;
    }
}
