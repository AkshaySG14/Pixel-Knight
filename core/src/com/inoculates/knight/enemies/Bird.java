package com.inoculates.knight.enemies;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Timer;
import com.inoculates.knight.screens.GameScreen;

public class Bird extends Creature {
    boolean vertical;
    Rectangle bounds1, bounds2;

    Animation fly;
    TextureAtlas.AtlasRegion fly1 = atlas.findRegion("run1");
    TextureAtlas.AtlasRegion fly2 = atlas.findRegion("run2");

    public Bird(GameScreen screen, TextureAtlas atlas, TiledMapTileLayer layer, TiledMap map, float sX, float sY,
                Rectangle one, Rectangle two, boolean vertical) {
        super(screen, atlas, layer, map, 1, 0, false, false, sX, sY);
        setX(sX);
        setY(sY);
        createAnimations();
        setSize(18, 18);
        setRegion(fly1);
        setState(IDLE);
        bounds1 = one;
        bounds2 = two;
        this.vertical = vertical;
        if (vertical)
            SVY(1);
        else SVX(1);
        dir = RIGHT;
    }

    public void draw(Batch batch) {
        super.draw(batch);
    }

    //If the bird is vertical, it will only move up and down depending on far it is from the spawn point. Otherwise
    //it will move left and right based on tis bounds.
    protected void tryMove()
    {
        if (vertical) {
            if (getY() + getHeight() > bounds1.getY())
                SVY(-1);
            else if (getY() < bounds2.getY())
                SVY(1);
        }
        else {
            if (getX() < bounds1.getX())
                SVX(1);
            else if (getX() > bounds2.getX() + bounds2.getWidth())
                SVX(-1);
        }

        dir = (int) Math.signum(vel.x);

        setY(getY() + vel.y);
        setX(getX() + vel.x);
    }

    protected void chooseSprite()
        {
            setRegion(fly.getKeyFrame(animationTime, true));
            setSize(fly.getKeyFrame(animationTime, true).getRegionWidth() / 2, fly.getKeyFrame(animationTime, true).getRegionHeight() / 2);
        }

    private void createAnimations() {
        fly = new Animation(0.25f, fly1, fly2);
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
            setFlip(true, false);
        else
            setFlip(false, false);
    }

    protected boolean harmful() {
        return true;
    }

    public boolean vulnerable(Object object) {
        return true;
    }
}
